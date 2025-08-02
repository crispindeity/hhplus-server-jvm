import csv, uuid, random
from faker import Faker
from datetime import datetime, timedelta
from pathlib import Path
import mysql.connector
import os

SCALE = int(os.environ.get("SCALE", "1"))

NUM_CONCERTS = 10 * SCALE
NUM_SCHEDULES = 1000 * SCALE
NUM_SEATS = 1000 * SCALE
NUM_USERS = 100_000 * SCALE
NUM_HELD_SEATS = 10_000 * SCALE
NUM_CONCERT_SEATS = 1_000_000 * SCALE
NUM_TRANSACTIONS = 300_000 * SCALE
NUM_RESERVATIONS = 200_000 * SCALE

fake = Faker()
now = datetime.now()
output_dir = Path("./csv")
output_dir.mkdir(exist_ok=True)

def timestamp():
    return now.strftime('%Y-%m-%d %H:%M:%S')

def write_csv(filename, rows):
    with open(output_dir / filename, "w", newline='') as f:
        writer = csv.writer(f)
        writer.writerows(rows)

concerts = []
for i in range(1, NUM_CONCERTS + 1):
    concerts.append([i, f"Concert {i}", timestamp(), timestamp()])
write_csv("concerts.csv", concerts)

schedules = []
schedule_ids = []
used_schedule_keys = set()

i = 1
while len(schedules) < NUM_SCHEDULES:
    concert_id = random.randint(1, NUM_CONCERTS)
    date = (now + timedelta(days=random.randint(1, 365))).strftime('%Y-%m-%d')
    key = (concert_id, date)

    if key in used_schedule_keys:
        continue

    used_schedule_keys.add(key)
    schedules.append([i, concert_id, date, timestamp(), timestamp()])
    schedule_ids.append(i)
    i += 1
write_csv("concert_schedules.csv", schedules)

seats = []
for i in range(1, NUM_SEATS + 1):
    number = i
    price = 150000
    seats.append([i, number, price, timestamp(), timestamp()])
write_csv("seats.csv", seats)

held_seat_ids = set()
seat_holds = []
users = []
user_ids = []
for i in range(1, NUM_USERS + 1):
    uid = str(uuid.uuid4())
    user_ids.append(uid)
    users.append([i, uid, timestamp(), timestamp()])
write_csv("users.csv", users)

for i in range(1, NUM_HELD_SEATS + 1):
    concert_seat_id = random.randint(1, NUM_CONCERT_SEATS)
    uid = random.choice(user_ids)
    held_at = now
    expires_at = held_at + timedelta(minutes=10)
    held_seat_ids.add(concert_seat_id)
    seat_holds.append([
        i, concert_seat_id, uid,
        held_at.strftime('%Y-%m-%d %H:%M:%S'),
        expires_at.strftime('%Y-%m-%d %H:%M:%S')
    ])
write_csv("seat_holds.csv", seat_holds)

concert_seats = []
for i in range(1, NUM_CONCERT_SEATS + 1):
    schedule_id = random.choice(schedule_ids)
    seat_id = random.randint(1, NUM_SEATS)
    if i in held_seat_ids:
        status = 'HELD'
    else:
        status = random.choices(['AVAILABLE', 'RESERVED'], weights=[0.9, 0.1])[0]
    concert_seats.append([i, schedule_id, seat_id, status, timestamp(), timestamp()])
    if i % 100000 == 0:
        print(f"concert_seats generated: {i}")
write_csv("concert_seats.csv", concert_seats)

wallets = []
transactions = []
tx_id = 1
for wallet_id, uid in enumerate(user_ids, start=1):
    num_tx = random.randint(1, 10)
    total_charged = 0
    total_used = 0
    for _ in range(num_tx):
        tx_type = random.choices(['CHARGED', 'USED'], weights=[0.5, 0.5])[0]
        if tx_type == 'CHARGED':
            amount = random.randint(1000, 50000)
            total_charged += amount
        else:
            max_usable = total_charged - total_used
            if max_usable < 1000:
                continue
            amount = random.randint(1000, min(50000, max_usable))
            total_used += amount
        transactions.append([tx_id, wallet_id, tx_type, amount, timestamp(), timestamp()])
        tx_id += 1
    balance = total_charged - total_used
    wallets.append([wallet_id, uid, balance, timestamp(), timestamp(), 0])
write_csv("point_wallets.csv", wallets)
write_csv("point_transactions.csv", transactions)

reservations = []
payments = []
for i in range(1, NUM_RESERVATIONS + 1):
    uid = random.choice(user_ids)
    concert_id = random.randint(1, NUM_CONCERTS)
    concert_seat_id = random.randint(1, NUM_CONCERT_SEATS)
    payment_status = random.choices(['PENDING', 'COMPLETED', 'CANCELLED'], weights=[0.2, 0.6, 0.2])[0]
    paid_at = timestamp() if payment_status == 'COMPLETED' else 'NULL'
    price = 150000
    payment_id = i
    payments.append([payment_id, uid, payment_status, price, paid_at, timestamp(), timestamp()])
    if payment_status == 'COMPLETED':
        res_status = 'CONFIRMED'
        confirmed_at = timestamp()
    elif payment_status == 'CANCELLED':
        res_status = 'CANCELLED'
        confirmed_at = 'NULL'
    else:
        res_status = random.choices(['IN_PROGRESS', 'EXPIRED'], weights=[0.7, 0.3])[0]
        confirmed_at = 'NULL'
    reserved_at = now
    expires_at = reserved_at + timedelta(minutes=10)
    reservations.append([
        i, uid, concert_id, payment_id, concert_seat_id,
        confirmed_at,
        reserved_at.strftime('%Y-%m-%d %H:%M:%S'),
        expires_at.strftime('%Y-%m-%d %H:%M:%S'),
        res_status, timestamp(), timestamp()
    ])
write_csv("payments.csv", payments)
write_csv("reservations.csv", reservations)

queue_tokens = []
for i in range(1, NUM_USERS + 1):
    uid = user_ids[i - 1]
    queue_number = i
    token = str(uuid.uuid4())
    status = 'COMPLETED'
    expires_at = now + timedelta(minutes=random.randint(5, 30))
    queue_tokens.append([
        i, uid, queue_number, token, status,
        expires_at.strftime('%Y-%m-%d %H:%M:%S'),
        timestamp(), timestamp()
    ])
write_csv("queue_tokens.csv", queue_tokens)

print("✅ CSV generation complete.")
print("✅ CSV 생성 완료. DB 삽입 시작.")

conn = mysql.connector.connect(
    host=os.environ["DB_HOST"],
    port=os.environ["DB_PORT"],
    user=os.environ["DB_USER"],
    password=os.environ["DB_PASSWORD"],
    database=os.environ["DB_NAME"]
)
cursor = conn.cursor()

def load_csv(table, columns, filename):
    path = f"/var/lib/mysql-files/{filename}"
    print(f"📥 {table} 테이블에 {filename} 삽입 중...")
    column_list = columns.split(',')
    var_columns = []
    set_clauses = []
    for col in column_list:
        if col in ["confirmed_at", "paid_at"]:
            var_columns.append(f"@{col}")
            set_clauses.append(f"{col} = NULLIF(@{col}, 'NULL')")
        else:
            var_columns.append(col)
    var_columns_str = ', '.join(var_columns)
    set_clause_str = ''
    if set_clauses:
        set_clause_str = f"SET {', '.join(set_clauses)}"
    sql = f"""
        LOAD DATA INFILE '{path}'
        INTO TABLE {table}
        FIELDS TERMINATED BY ','
        LINES TERMINATED BY '\\n'
        ({var_columns_str})
        {set_clause_str};
    """
    cursor.execute(sql)

csv_definitions = [
    ("users", "id,user_id,created_at,updated_at", "users.csv"),
    ("concerts", "id,title,created_at,updated_at", "concerts.csv"),
    ("concert_schedules", "id,concert_id,date,created_at,updated_at", "concert_schedules.csv"),
    ("concert_seats", "id,schedule_id,seat_id,status,created_at,updated_at", "concert_seats.csv"),
    ("seats", "id,number,price,created_at,updated_at", "seats.csv"),
    ("point_wallets", "id,user_id,balance,created_at,updated_at,version", "point_wallets.csv"),
    ("reservations", "id,user_id,concert_id,payment_id,concert_seat_id,confirmed_at,reserved_at,expires_at,status,created_at,updated_at", "reservations.csv"),
    ("point_transactions", "id,point_wallet_id,type,amount,created_at,updated_at", "point_transactions.csv"),
    ("payments", "id,user_id,status,price,paid_at,created_at,updated_at", "payments.csv"),
    ("seat_holds", "id,concert_seat_id,user_id,held_at,expires_at", "seat_holds.csv"),
    ("queue_tokens", "id,user_id,queue_number,token,status,expires_at,created_at,updated_at", "queue_tokens.csv"),
]

for table, columns, filename in csv_definitions:
    load_csv(table, columns, filename)


def reset_autoincrement(table, id_column):
    cursor.execute(f"SELECT MAX({id_column}) FROM {table}")
    max_id = cursor.fetchone()[0] or 0
    next_id = max_id + 1
    print(f"🔧 {table} AUTO_INCREMENT를 {next_id}로 설정 중...")
    cursor.execute(f"ALTER TABLE {table} AUTO_INCREMENT = {next_id}")

reset_autoincrement("users", "id")
reset_autoincrement("concerts", "id")
reset_autoincrement("concert_schedules", "id")
reset_autoincrement("concert_seats", "id")
reset_autoincrement("seats", "id")
reset_autoincrement("point_wallets", "id")
reset_autoincrement("point_transactions", "id")
reset_autoincrement("payments", "id")
reset_autoincrement("reservations", "id")
reset_autoincrement("seat_holds", "id")
reset_autoincrement("queue_tokens", "id")

conn.commit()
conn.close()
print("✅ 모든 데이터 삽입 완료.")
