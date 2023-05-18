package koumakan.javaweb.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @Package: koumakan.javaweb.community
 * @Author: Alice Maetra
 * @Date: 2023/5/7 16:54
 * @Decription:
 */
public class BlockQueueTest {

    private static BlockingQueue<Integer> BQ = new ArrayBlockingQueue<>(10);

    public static void main(String[] args) throws InterruptedException {
        TestConsumer tc = new TestConsumer(BQ);
        Producer pc = new Producer(BQ);

        new Thread(tc, "csm1").start();
        new Thread(tc, "csm2").start();
        new Thread(tc, "csm3").start();
        new Thread(pc, "pdc1").start();
        // new Thread(pc, "pdc2").start();


        // Thread.currentThread().wait();
    }



}

class Producer implements Runnable {

    private BlockingQueue<Integer> bq;

    public Producer(BlockingQueue<Integer> b) {
        this.bq = b;
    }

    @Override
    public void run() {
        for (int i = 0 ; i < 20; ++i) {
            try {
                bq.put(1);
                System.out.println(Thread.currentThread().getName() + ": size -> " + bq.size());
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

class TestConsumer implements Runnable {

    private BlockingQueue<Integer> bq;

    public TestConsumer(BlockingQueue<Integer> b) {
        this.bq = b;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(new Random().nextInt(1000));
            bq.take();
            System.out.println(Thread.currentThread().getName() + ": size -> " + bq.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
