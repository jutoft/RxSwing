/**
 * Copyright 2014 Netflix, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.swing.sources;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.SwingScheduler;

/* package-private */final class SwingTestHelper { // only for test

    private final CountDownLatch latch = new CountDownLatch(1);
    private volatile Throwable error;

    private SwingTestHelper() {
    }

    public static SwingTestHelper create() {
        return new SwingTestHelper();
    }

    public SwingTestHelper runInEventDispatchThread(final Action action) {
        Scheduler.Worker inner = SwingScheduler.getInstance().createWorker();
        inner.schedule(new Runnable() {

            @Override
            public void run() {
                try {
                    action.run();
                } catch (Throwable e) {
                    error = e;
                }
                latch.countDown();
            }
        });
        return this;
    }

    public void awaitTerminal() throws Throwable {
        latch.await();
        if (error != null) {
            throw error;
        }
    }

    public void awaitTerminal(long timeout, TimeUnit unit) throws Throwable {
        latch.await(timeout, unit);
        if (error != null) {
            throw error;
        }
    }

}
