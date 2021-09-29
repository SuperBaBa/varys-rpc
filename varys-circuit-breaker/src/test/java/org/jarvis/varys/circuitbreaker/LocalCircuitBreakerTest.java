package org.jarvis.varys.circuitbreaker;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;


public class LocalCircuitBreakerTest {
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
                            // 模拟后期的服务恢复状态
                            if (countDownLatch.getCount() >= maxNum / 2) {
                                // 模拟随机失败
                                if (new Random().nextInt(2) == 1) {
                                    throw new Exception("mock error");
                                }
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

            // 模拟随机请求
            try {
                Thread.sleep(new Random().nextInt(5) * 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end");
    }

    @Test
    public void testByCyclicBarrier() {
        final int maxNum = 200;
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(maxNum);

        final LocalCircuitBreaker circuitBreaker = LocalCircuitBreaker.getInstance();
        circuitBreaker.setSwitchOpenThresholdCount("5/20");// 20秒内如果出现5个失败，则进入熔断打开状态
        circuitBreaker.setReopenThresholdCountAtHalf(6);// 半开状态如果出现6个失败，则重新打开熔断
        circuitBreaker.setRetryThresholdAtHalf("10/50");// 50秒放10个请求进行尝试
        circuitBreaker.setSwitchHalfThresholdTimeAtOpen(10);// 熔断10秒后进行半开状态

        System.out.println("start ---" + Thread.currentThread());
        for (int i = 0; i < maxNum; i++) {
            Worker worker = new Worker(cyclicBarrier, circuitBreaker);
            worker.start();
        }
        System.out.println("end ---" + Thread.currentThread());
    }

    static class Worker extends Thread {
        private final CyclicBarrier cyclicBarrier;
        private final CircuitBreaker circuitBreaker;

        public Worker(CyclicBarrier cyclicBarrier, CircuitBreaker circuitBreaker) {
            this.cyclicBarrier = cyclicBarrier;
            this.circuitBreaker = circuitBreaker;
        }

        @Override
        public void run() {
            try {
                if (circuitBreaker.isPassCheck()) {
                    // do something
                    System.out.println("正常业务逻辑操作" + Thread.currentThread().getName());
                    long sleepTime = new Random().nextInt(3) * 1000L;
                    Thread.sleep(sleepTime);
                    cyclicBarrier.await();// 执行完后挂起所有线程，等待所有执行后并发进行
                    Long timestamp = System.currentTimeMillis();
                    // 模拟后期的服务恢复状态
                    if (timestamp % 2 == 0) {
                        // 模拟随机失败
                        throw new RuntimeException("mock error");
                    }
                } else {
                    System.out.println("拦截业务逻辑操作");
                }
            } catch (Exception e) {
                System.out.println("发生异常统计");
                circuitBreaker.countFailNum();// 统计异常
            }
        }
    }
}