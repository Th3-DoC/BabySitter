//package th3doc.babysitter.utils.async;
//
//
//public class Threading
//{
//    final private Main main;
//
//    public Threading(Main main) { this.main = main; }
//
//    public <R> R alwaysSync(Task<R> task) {
//        if (Thread.currentThread().getId() != this.main.getThread().getId()) // Create your own Main.getThread()
//            return new ReturningRunnable<R>() {
//                @Override
//                public void run() {
//                    setR(task.run());
//                }
//            }.run(this.main).getR(); // Main.get just returns the instance of Main.
//        return task.run();
//    }
//
//    private static abstract class ReturningRunnable<R> extends BukkitRunnable
//    {
//        private R ret = null;
//
//        @Override
//        public abstract void run();
//
//        public synchronized ReturningRunnable<R> run(Main main) {
//            runTask(main);
//            return this;
//        }
//
//        public R getR() {
//            return ret;
//        }
//
//        protected void setR(R r) {
//            if (ret != null)
//                return;
//            ret = r;
//        }
//    }
//}
