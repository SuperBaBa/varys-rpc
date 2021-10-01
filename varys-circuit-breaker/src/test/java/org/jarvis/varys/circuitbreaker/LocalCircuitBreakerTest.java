package org.jarvis.varys.circuitbreaker;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;


public class LocalCircuitBreakerTest {
    private final Logger log = LoggerFactory.getLogger(LocalCircuitBreakerTest.class);

    @Test
    public void testLogger() {
        log.info("test log print on console");
    }

    @Test
    public void testByCountDownLatch() {

        final int maxNum = 200;
        final CountDownLatch countDownLatch = new CountDownLatch(maxNum);

        final LocalCircuitBreaker circuitBreaker = LocalCircuitBreaker.getInstance();
        circuitBreaker.setSwitchOpenThresholdCount("5/20");// 20秒内如果出现5个失败，则进入熔断打开状态
        circuitBreaker.setReopenThresholdCountAtHalf(3);// 半开状态如果出现6个失败，则重新打开熔断
        circuitBreaker.setRetryThresholdAtHalf("5/10");// 50秒放10个请求进行尝试
        circuitBreaker.setSwitchHalfThresholdTimeAtOpen(10);// 熔断10秒后进行半开状态

        for (int i = 0; i < maxNum; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(new Random().nextInt(20) * 1000);
                        // 检查是否通过熔断器检查, 若未通过则直接被拦截
                        if (circuitBreaker.isPassCheck()) {
                            // do something
                            System.out.println("正常业务逻辑操作");
                            // 模拟随机失败
                            if (countDownLatch.getCount() >= maxNum / 2 && new Random().nextInt(2) == 1) {
                                throw new RuntimeException("mock error");
                            }
                        } else {
                            System.out.println("拦截业务逻辑操作");
                        }
                    } catch (Exception e) {
                        System.out.println("业务执行失败了");
                        // 熔断器计数器
                        circuitBreaker.countFailNum();
                    }
                    countDownLatch.countDown();
                }
            }).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end");
    }

    @Test
    public void testByCyclicBarrier() throws BrokenBarrierException, InterruptedException {
        final int maxNum = 500;
        final CountDownLatch countDownLatch = new CountDownLatch(maxNum);
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(maxNum);

        final LocalCircuitBreaker circuitBreaker = LocalCircuitBreaker.getInstance();
        circuitBreaker.setSwitchOpenThresholdCount("5/20");// 20秒内如果出现5个失败，则进入熔断打开状态
        circuitBreaker.setReopenThresholdCountAtHalf(25);// 半开状态如果出现25个失败，则重新打开熔断
        circuitBreaker.setRetryThresholdAtHalf("50/10");// 10秒放50个请求进行尝试
        circuitBreaker.setSwitchHalfThresholdTimeAtOpen(3);// 熔断10秒后进行半开状态

        log.info("--- start --- ");
        for (int i = 0; i < maxNum; i++) {
            Worker worker = new Worker(cyclicBarrier, circuitBreaker, countDownLatch);
            worker.start();
        }
        countDownLatch.await();
        //cyclicBarrier.await();// 执行完后挂起所有线程，等待所有执行后并发进行
        log.info("--- end ---");
    }

    static class Worker extends Thread {
        private final Logger log = LoggerFactory.getLogger(Worker.class);
        private final CyclicBarrier cyclicBarrier;
        private final CircuitBreaker circuitBreaker;
        private final CountDownLatch countDownLatch;


        public Worker(CyclicBarrier cyclicBarrier, CircuitBreaker circuitBreaker, CountDownLatch countDownLatch) {
            this.cyclicBarrier = cyclicBarrier;
            this.circuitBreaker = circuitBreaker;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                int sleepTime = new Random().nextInt(20);
                Thread.sleep(new Random().nextInt(20) * 1000);
                if (circuitBreaker.isPassCheck()) {
                    // do something
                    // 模拟后期的服务恢复状态
                    if (sleepTime < 10) {
                        // 模拟随机失败
                        throw new RuntimeException("mock error");
                    }
                    log.info("正常业务逻辑操作结束");
                } else {
                    log.info("拦截业务逻辑操作");
                }
            } catch (Exception e) {
                circuitBreaker.countFailNum();// 统计异常
                log.warn("发生异常统计");
            }finally {
                countDownLatch.countDown();
            }

        }
    }

    @Test
    public void testAtomicInt() throws Exception {
        AtomicInteger atomicInteger = new AtomicInteger();
        int threadNum = 500;
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum, new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " 完成最后任务");
            }
        });
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " 到达栅栏，准备自增");
                    cyclicBarrier.await();
                    atomicInteger.incrementAndGet();
                    System.out.println(Thread.currentThread().getName() + " 到达栅栏，可以进行countdown");
                    cyclicBarrier.await();
                    countDownLatch.countDown();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        countDownLatch.await();
        log.info("最终结果为: {}", atomicInteger.get());
        Assert.assertEquals("最终结果并不是线程安全", threadNum, atomicInteger.get());
    }
}