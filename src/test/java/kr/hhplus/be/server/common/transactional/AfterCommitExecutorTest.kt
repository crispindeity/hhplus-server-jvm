package kr.hhplus.be.server.common.transactional

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class AfterCommitExecutorTest :
    BehaviorSpec({
        lateinit var executor: AfterCommitExecutor

        beforeTest {
            executor = AfterCommitExecutor()
        }

        afterTest {
            executor.clearIfStackEmpty()
        }

        context("단독 트랜잭션") {
            given("afterCommit 콜백을 등록하면") {
                `when`("커밋 시") {
                    then("커밋 직후 즉시 실행된다.") {
                        val hits: MutableList<String> = mutableListOf()

                        executor.pushTransactionScope()
                        executor.registerAfterCommit { hits += "A" }
                        executor.registerAfterCommit { hits += "B" }

                        executor.onTransactionCommit(isNewTransaction = true)

                        hits shouldBe listOf("A", "B")
                    }
                }
            }

            given("afterRollback 콜백을 등록하면") {
                `when`("롤백 시") {
                    then("롤백 직후 즉시 실행된다.") {
                        val hits: MutableList<String> = mutableListOf()

                        executor.pushTransactionScope()
                        executor.registerAfterRollback { hits += "R1" }
                        executor.registerAfterRollback { hits += "R2" }

                        executor.onTransactionRollback(isNewTransaction = true)

                        hits shouldBe listOf("R1", "R2")
                    }
                }
            }
        }

        context("중첩 트랜잭션(REQUIRED)") {
            given("afterCommit 콜백을 등록하면") {
                `when`("자식 트랜잭션 커밋 시") {
                    then("콜백은 즉시 실행되지 않고 부모 트랜잭션에서 실행된다.") {
                        val hits: MutableList<String> = mutableListOf()

                        executor.pushTransactionScope()
                        executor.pushTransactionScope()

                        executor.registerAfterCommit { hits += "inner-commit" }
                        executor.registerAfterRollback { hits += "inner-rollback" }

                        executor.onTransactionCommit(isNewTransaction = false)

                        hits.shouldBeEmpty()

                        executor.onTransactionCommit(isNewTransaction = true)

                        hits shouldBe listOf("inner-commit")
                    }
                }
            }

            given("afterRollback 콜백을 등록하면") {
                `when`("자식 트랜잭션 롤백 시") {
                    then("rollback 콜백만 부모 트랜잭션에서 실행되고 commit 콜백은 동작하지 않는다.") {
                        val hits: MutableList<String> = mutableListOf()

                        executor.pushTransactionScope()
                        executor.pushTransactionScope()

                        executor.registerAfterCommit { hits += "inner-commit" }
                        executor.registerAfterRollback { hits += "inner-rollback" }

                        executor.onTransactionRollback(isNewTransaction = false)

                        hits.shouldBeEmpty()

                        executor.onTransactionRollback(isNewTransaction = true)

                        hits shouldBe listOf("inner-rollback")
                    }
                }
            }
        }

        context("중첩 트랜잭션(REQUIRES_NEW)") {
            given("afterCommit 콜백을 등록하면") {
                `when`("내부 트랜잭션 커밋 시") {
                    then("내부 트랜잭션은 즉시 실행되고, 상위 트랜잭션과 독립적으로 동작한다.") {
                        val hits: MutableList<String> = mutableListOf()

                        executor.pushTransactionScope()
                        executor.registerAfterCommit { hits += "outer-commit" }

                        executor.pushTransactionScope()
                        executor.registerAfterCommit { hits += "inner-commit" }

                        executor.onTransactionCommit(isNewTransaction = true)
                        hits shouldBe listOf("inner-commit")

                        executor.onTransactionCommit(isNewTransaction = true)
                        hits shouldBe listOf("inner-commit", "outer-commit")
                    }
                }
            }
        }

        context("콜백 우선순위(priority") {
            given("우선순위가 다른 콜백을 등록하면") {
                `when`("커밋 시") {
                    then("priority가 내림차순으로 실행된다.") {
                        val hits: MutableList<String> = mutableListOf()

                        executor.pushTransactionScope()
                        executor.registerAfterCommit(priority = 5) { hits += "5" }
                        executor.registerAfterCommit(priority = 10) { hits += "10" }
                        executor.registerAfterCommit(priority = 1) { hits += "1" }

                        executor.onTransactionCommit(isNewTransaction = true)

                        hits shouldBe listOf("10", "5", "1")
                    }
                }
            }
        }

        context("콜백 예외 발생") {
            given("여러 콜백 등록 후") {
                `when`("하나가 예외를 던졌을 때") {
                    then("나머지 콜백은 계속 실행된다.") {
                        val hits: MutableList<String> = mutableListOf()

                        executor.pushTransactionScope()
                        executor.registerAfterCommit { hits += "200ok-1" }
                        executor.registerAfterCommit { error("500error") }
                        executor.registerAfterCommit { hits += "200ok-2" }

                        executor.onTransactionCommit(isNewTransaction = true)
                        hits shouldBe listOf("200ok-1", "200ok-2")
                    }
                }
            }
        }

        context("스택 관리") {
            given("스택이 비어 있을 때") {
                `when`("clearIfStackEmpty 호출") {
                    then("ThreadLocal을 안전하게 정리한다.") {
                        executor.pushTransactionScope()
                        executor.onTransactionCommit(isNewTransaction = true)
                        executor.clearIfStackEmpty()

                        shouldNotThrowAny {
                            executor.pushTransactionScope()
                            executor.onTransactionCommit(isNewTransaction = true)
                            executor.clearIfStackEmpty()
                        }
                    }
                }
            }
        }
    })
