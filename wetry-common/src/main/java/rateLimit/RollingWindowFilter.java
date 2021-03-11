package rateLimit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RollingWindowFilter extends AbstractLimiter {

    /**
     * 我们的滑动窗口对象,包含多个窗口
     */
    private final Windows windows;

    /**
     * 程序中暴露的唯一一个计数器,可以称之为当前窗口
     */
    private AtomicInteger counter;

    /**
     * 计数器初始化大小
     */
    private static final int INIT_SIZE = 0;

    /**
     * 比如窗口分为10块,这个代表先进入9块窗口的计算值 , 为什么要引入是因为不浪费计算资源, 好多都是重复计算
     */
    private final AtomicInteger preCount;


    /**
     * 我们默认队列大小是 20 ,其实颗粒度很高了50ms计算一次, 可以重载构造参数调整
     *
     * @param MAX_FlOW 最大流量
     */
    public RollingWindowFilter(int MAX_FlOW) {

        super(MAX_FlOW);

        windows = new Windows(20);

        // 初始化对象
        preCount = new AtomicInteger(INIT_SIZE);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                ArrayBlockingQueue<AtomicInteger> queue = windows.blocks;

                // 当前窗口大小
                int size = queue.size();

                /**
                 * 初始化窗口长度
                 */
                if (size < windows.capacity) {
                    try {

                        /**
                         * 计算前面窗口的计数器总和
                         * 
                         * 这里其实由多线程的并发问题 ,其实可以设置一个标识符来表示完成与否 .. 我懒得改了 ,或者你就大量实例化对象,不用我这个单一对象
                         */
                        preCount.set(INIT_SIZE);
                        if (size > 0) {
                            queue.forEach(e -> preCount.addAndGet(e.get()));
                        }

                        // 新建一个计数器, 放入对应的滑块 ,其实就是队尾
                        counter = new AtomicInteger(INIT_SIZE);
                        queue.put(counter);
                    } catch (InterruptedException e) {
                        //
                    }

                }

                /**
                 * 当窗口长度初始化完成
                 */
                if (size == windows.capacity) {

                    try {
                        // 出局最先进来的那个
                        queue.take();

                        // 计算前面窗口的计数器总和 , 有多线程并发问题
                        preCount.set(INIT_SIZE);
                        queue.forEach(e -> preCount.addAndGet(e.get()));

                        
                        // 新建一个计数器, 放入对应的滑块 ,其实就是队尾
                        counter = new AtomicInteger(INIT_SIZE);
                        queue.put(counter);
                    } catch (InterruptedException e) {
                        //
                    }
                }
            }
        }, 0, 1000 / windows.capacity);
    }


    public void limit(ServletRequest request, ServletResponse response, FilterChain chain) {

        int cur = counter.get();
        int pre = preCount.get();
        int sum = cur + pre;

        if (sum < MAX_FlOW) {
            counter.incrementAndGet();
            try {
                chain.doFilter(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 滑块组成 , 一个队列维护一个块 , 其实可以用LinkedList来维护 , 我是懒得改
     * <p>
     * 一般内部类来说看JDK源码你会发现都会用private static修饰 ,因为反射不是静态内部类,无法实例化 , 和构造器不加修饰
     */
    private static class Windows {
        // 多少个计数器
        private final int capacity;
        // 放置计数器
        private final ArrayBlockingQueue<AtomicInteger> blocks;

        Windows(int capacity) {
            this.blocks = new ArrayBlockingQueue<>(capacity);
            this.capacity = capacity;
        }
    }
}
