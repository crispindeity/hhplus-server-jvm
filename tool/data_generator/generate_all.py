import csv, uuid, random, gc, logging
from faker import Faker
from datetime import datetime, timedelta
from pathlib import Path
import mysql.connector
from mysql.connector import pooling
import os
from typing import Iterator, List, Tuple
import psutil
from concurrent.futures import ThreadPoolExecutor, as_completed
import multiprocessing as mp
from contextlib import contextmanager

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('data_generator.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

SCALE = int(os.environ.get("SCALE", "1"))
CHUNK_SIZE = int(os.environ.get("CHUNK_SIZE", "100000"))
MAX_WORKERS = int(os.environ.get("MAX_WORKERS", str(min(mp.cpu_count() * 2, 16))))

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

def get_memory_usage():
    process = psutil.Process(os.getpid())
    return process.memory_info().rss / 1024 / 1024

def log_memory(step: str):
    memory_mb = get_memory_usage()
    logger.info(f"📊 {step}: {memory_mb:.1f}MB")

@contextmanager
def memory_monitor(step: str):
    start_memory = get_memory_usage()
    logger.info(f"🚀 {step} 시작 - 메모리: {start_memory:.1f}MB")
    try:
        yield
    finally:
        end_memory = get_memory_usage()
        logger.info(f"✅ {step} 완료 - 메모리: {end_memory:.1f}MB (변화: {end_memory-start_memory:+.1f}MB)")
        gc.collect()

def timestamp():
    return now.strftime('%Y-%m-%d %H:%M:%S')

def write_csv_chunked(filename: str, data_generator: Iterator, chunk_size: int = CHUNK_SIZE):
    filepath = output_dir / filename
    with open(filepath, "w", newline='', buffering=1024*1024) as f:
        writer = csv.writer(f)
        chunk = []

        for row in data_generator:
            chunk.append(row)
            if len(chunk) >= chunk_size:
                writer.writerows(chunk)
                f.flush()
                chunk.clear()

        if chunk:
            writer.writerows(chunk)
            f.flush()

def generate_concerts() -> Iterator[List]:
    for i in range(1, NUM_CONCERTS + 1):
        yield [i, f"Concert {i}", timestamp(), timestamp()]

def generate_schedules() -> Tuple[Iterator[List], List[int]]:
    schedule_ids = []
    used_schedule_keys = set()

    def schedule_generator():
        nonlocal schedule_ids
        i = 1
        generated_count = 0

        while generated_count < NUM_SCHEDULES:
            concert_id = random.randint(1, NUM_CONCERTS)
            date = (now + timedelta(days=random.randint(1, 365))).strftime('%Y-%m-%d')
            key = (concert_id, date)

            if key in used_schedule_keys:
                continue

            used_schedule_keys.add(key)
            schedule_ids.append(i)
            generated_count += 1
            yield [i, concert_id, date, timestamp(), timestamp()]
            i += 1

    return schedule_generator(), schedule_ids

def generate_seats() -> Iterator[List]:
    for i in range(1, NUM_SEATS + 1):
        yield [i, i, 150000, timestamp(), timestamp()]

def generate_users() -> Tuple[Iterator[List], List[str]]:
    user_ids = []

    def user_generator():
        nonlocal user_ids
        for i in range(1, NUM_USERS + 1):
            uid = str(uuid.uuid4())
            user_ids.append(uid)
            yield [i, uid, timestamp(), timestamp()]

            if i % 10000 == 0:
                gc.collect()

    return user_generator(), user_ids

def generate_seat_holds(user_ids: List[str]) -> Tuple[Iterator[List], set]:
    held_seat_ids = set()

    def seat_hold_generator():
        nonlocal held_seat_ids
        for i in range(1, NUM_HELD_SEATS + 1):
            concert_seat_id = random.randint(1, NUM_CONCERT_SEATS)
            uid = random.choice(user_ids)
            held_at = now
            expires_at = held_at + timedelta(minutes=10)
            held_seat_ids.add(concert_seat_id)

            yield [
                i, concert_seat_id, uid,
                held_at.strftime('%Y-%m-%d %H:%M:%S'),
                expires_at.strftime('%Y-%m-%d %H:%M:%S')
            ]

    return seat_hold_generator(), held_seat_ids

def generate_concert_seats(schedule_ids: List[int], held_seat_ids: set) -> Iterator[List]:
    for i in range(1, NUM_CONCERT_SEATS + 1):
        schedule_id = random.choice(schedule_ids)
        seat_id = random.randint(1, NUM_SEATS)

        if i in held_seat_ids:
            status = 'HELD'
        else:
            status = random.choices(['AVAILABLE', 'RESERVED'], weights=[0.9, 0.1])[0]

        yield [i, schedule_id, seat_id, status, timestamp(), timestamp()]

        if i % 100000 == 0:
            gc.collect()

def generate_wallets_and_transactions(user_ids: List[str]) -> Tuple[List[List], List[List]]:
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

        if wallet_id % 10000 == 0:
            gc.collect()

    return wallets, transactions

def generate_reservations_and_payments(user_ids: List[str]) -> Tuple[List[List], List[List]]:
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

        if i % 10000 == 0:
            gc.collect()

    return reservations, payments

def generate_queue_tokens(user_ids: List[str]) -> Iterator[List]:
    for i in range(1, NUM_USERS + 1):
        uid = user_ids[i - 1]
        queue_number = i
        token = str(uuid.uuid4())
        status = 'COMPLETED'
        expires_at = now + timedelta(minutes=random.randint(5, 30))

        yield [
            i, uid, queue_number, token, status,
            expires_at.strftime('%Y-%m-%d %H:%M:%S'),
            timestamp(), timestamp()
        ]

def create_connection_pool():
    config = {
        'host': os.environ["DB_HOST"],
        'port': os.environ["DB_PORT"],
        'user': os.environ["DB_USER"],
        'password': os.environ["DB_PASSWORD"],
        'database': os.environ["DB_NAME"],
        'pool_name': 'mypool',
        'pool_size': min(MAX_WORKERS, 32),
        'pool_reset_session': True,
        'autocommit': True,
        'use_unicode': True,
        'charset': 'utf8mb4',
        'sql_mode': 'NO_AUTO_VALUE_ON_ZERO',
        'connect_timeout': 60,
        'buffered': True
    }
    return pooling.MySQLConnectionPool(**config)

def load_csv_optimized(pool, table: str, columns: str, filename: str):
    conn = pool.get_connection()
    try:
        cursor = conn.cursor()
        path = f"/var/lib/mysql-files/{filename}"
        logger.info(f"📥 {table} 테이블에 {filename} 삽입 중...")

        cursor.execute("SET SESSION foreign_key_checks = 0")
        cursor.execute("SET SESSION unique_checks = 0")

        try:
            cursor.execute(f"ALTER TABLE {table} DISABLE KEYS")
        except:
            pass

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

        try:
            cursor.execute(f"ALTER TABLE {table} ENABLE KEYS")
        except:
            pass

        cursor.execute("SET SESSION foreign_key_checks = 1")
        cursor.execute("SET SESSION unique_checks = 1")

        logger.info(f"✅ {table} 삽입 완료")

    finally:
        conn.close()

def reset_autoincrement_optimized(pool, table: str, id_column: str):
    conn = pool.get_connection()
    try:
        cursor = conn.cursor()
        cursor.execute(f"SELECT MAX({id_column}) FROM {table}")
        max_id = cursor.fetchone()[0] or 0
        next_id = max_id + 1
        logger.info(f"🔧 {table} AUTO_INCREMENT를 {next_id}로 설정 중...")
        cursor.execute(f"ALTER TABLE {table} AUTO_INCREMENT = {next_id}")
    finally:
        conn.close()

def main():
    logger.info("🚀 메모리 최적화된 CSV 생성 시작")
    log_memory("시작")

    with memory_monitor("콘서트 데이터 생성"):
        write_csv_chunked("concerts.csv", generate_concerts())

    with memory_monitor("스케줄 데이터 생성"):
        schedule_gen, schedule_ids = generate_schedules()
        write_csv_chunked("concert_schedules.csv", schedule_gen)

    with memory_monitor("좌석 데이터 생성"):
        write_csv_chunked("seats.csv", generate_seats())

    with memory_monitor("사용자 데이터 생성"):
        user_gen, user_ids = generate_users()
        write_csv_chunked("users.csv", user_gen)

    with memory_monitor("좌석 홀드 데이터 생성"):
        seat_hold_gen, held_seat_ids = generate_seat_holds(user_ids)
        write_csv_chunked("seat_holds.csv", seat_hold_gen)

    with memory_monitor("콘서트 좌석 데이터 생성"):
        write_csv_chunked("concert_seats.csv", generate_concert_seats(schedule_ids, held_seat_ids))

    with memory_monitor("지갑 및 거래 데이터 생성"):
        wallets, transactions = generate_wallets_and_transactions(user_ids)
        write_csv_chunked("point_wallets.csv", iter(wallets))
        write_csv_chunked("point_transactions.csv", iter(transactions))
        del wallets, transactions

    with memory_monitor("예약 및 결제 데이터 생성"):
        reservations, payments = generate_reservations_and_payments(user_ids)
        write_csv_chunked("reservations.csv", iter(reservations))
        write_csv_chunked("payments.csv", iter(payments))
        del reservations, payments

    with memory_monitor("대기열 토큰 데이터 생성"):
        write_csv_chunked("queue_tokens.csv", generate_queue_tokens(user_ids))

    logger.info("✅ CSV 생성 완료.")
    log_memory("CSV 생성 완료")

    del user_ids, schedule_ids, held_seat_ids
    gc.collect()

    logger.info("🗄️ DB 삽입 시작.")

    with memory_monitor("DB 연결 및 데이터 삽입"):
        pool = create_connection_pool()

        dependency_order = [
            ("users", "id,user_id,created_at,updated_at", "users.csv"),
            ("concerts", "id,title,created_at,updated_at", "concerts.csv"),
            ("seats", "id,number,price,created_at,updated_at", "seats.csv"),
            ("concert_schedules", "id,concert_id,date,created_at,updated_at", "concert_schedules.csv"),
            ("point_wallets", "id,user_id,balance,created_at,updated_at,version", "point_wallets.csv"),
        ]

        parallel_batch_1 = [
            ("concert_seats", "id,schedule_id,seat_id,status,created_at,updated_at", "concert_seats.csv"),
            ("seat_holds", "id,concert_seat_id,user_id,held_at,expires_at", "seat_holds.csv"),
            ("point_transactions", "id,point_wallet_id,type,amount,created_at,updated_at", "point_transactions.csv"),
            ("payments", "id,user_id,status,price,paid_at,created_at,updated_at", "payments.csv"),
            ("queue_tokens", "id,user_id,queue_number,token,status,expires_at,created_at,updated_at", "queue_tokens.csv"),
        ]

        parallel_batch_2 = [
            ("reservations", "id,user_id,concert_id,payment_id,concert_seat_id,confirmed_at,reserved_at,expires_at,status,created_at,updated_at", "reservations.csv"),
        ]

        for batch_name, batch in [("의존성 테이블", dependency_order), ("병렬 배치 1", parallel_batch_1), ("병렬 배치 2", parallel_batch_2)]:
            logger.info(f"🔄 {batch_name} 삽입 시작")
            with ThreadPoolExecutor(max_workers=MAX_WORKERS if len(batch) > 1 else 1) as executor:
                futures = []
                for table, columns, filename in batch:
                    future = executor.submit(load_csv_optimized, pool, table, columns, filename)
                    futures.append(future)

                for future in as_completed(futures):
                    try:
                        future.result()
                    except Exception as e:
                        logger.error(f"❌ 오류 발생: {e}")

        reset_tables = [
            ("users", "id"), ("concerts", "id"), ("concert_schedules", "id"),
            ("concert_seats", "id"), ("seats", "id"), ("point_wallets", "id"),
            ("point_transactions", "id"), ("payments", "id"), ("reservations", "id"),
            ("seat_holds", "id"), ("queue_tokens", "id")
        ]

        with ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
            futures = []
            for table, id_column in reset_tables:
                future = executor.submit(reset_autoincrement_optimized, pool, table, id_column)
                futures.append(future)

            for future in as_completed(futures):
                try:
                    future.result()
                except Exception as e:
                    logger.error(f"❌ AUTO_INCREMENT 리셋 오류: {e}")

    logger.info("✅ 모든 데이터 삽입 완료.")
    log_memory("완료")

if __name__ == "__main__":
    main()
