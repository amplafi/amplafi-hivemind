/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package org.amplafi.hivemind.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.amplafi.hivemind.util.SwitchableThreadLocal.ThreadLocalMode.*;


/**
 * Class that inherits {@link InheritableThreadLocal} but provides an ability to be switched to the mode,
 * when it shares the single value across all threads. There are few modes available: look at
 * {@link org.amplafi.hivemind.util.SwitchableThreadLocal.ThreadLocalMode} enumeration and it's
 * documentation. It's supposed to have more than two modes in the future and be able to adjust class
 * accordingly. That is the reason for enumeration usage.
 * <p/>
 * It's thread safe to get/remove/set values to the instance of the class in the usage other than
 * classic {@link InheritableThreadLocal}. But it is still in the responsibility of caller to synchronize usage
 * of value object outside the class.
 *
 * @author Denis Rogov
 * @param <T>
 */
public class SwitchableThreadLocal<T> extends InheritableThreadLocal<T> {
    /**
     * Root variable to contain the mode this instance is performing in.
     *
     * @see org.amplafi.hivemind.util.SwitchableThreadLocal.ThreadLocalMode
     */
    private ThreadLocalMode mode;

    /**
     * Lock for synchronization in {@link ThreadLocalMode#SHARE_VALUE_FOR_ALL_THREADS} mode.
     */
    private Lock lock = new ReentrantLock();

    /**
     * Used only in {@link ThreadLocalMode#SHARE_VALUE_FOR_ALL_THREADS} mode. It contains a value
     * set through all threads. Might carry null value. This situation is controlled by
     * {@link #valueInitialized} variable.
     */
    private T value;
    /**
     * Used only in {@link ThreadLocalMode#SHARE_VALUE_FOR_ALL_THREADS} mode. It indicates whether
     * a value has been set for all threads. Works in pair with {@link #value} field.
     */
    private boolean valueInitialized;

    public SwitchableThreadLocal(ThreadLocalMode mode) {
        this.mode = mode;
    }

    /**
     * When there is enough to set the information whether the instance should share value across
     * threads or not.
     *
     * @param shareAcrossThreads whether the instance should share value across threads or not.
     */
    public SwitchableThreadLocal(boolean shareAcrossThreads) {
        this(getDefaultMode(shareAcrossThreads));
    }

    /**
     * Using this method note, that there is no guarantee that someone has used the instance with
     * another mode and thus some values could be lost till returing to the same mode.
     *
     * @param shareAcrossThreads whether the instance should share value across threads or not.
     */
    public void setMode(boolean shareAcrossThreads) {
        setMode(mode = getDefaultMode(shareAcrossThreads));
    }

    /**
     * /**
     * Using this method note, that there is no guarantee that someone has used the instance with
     * another mode and thus some values could be lost till returing to the same mode.
     *
     * @param mode mode to act from now.
     */
    public void setMode(ThreadLocalMode mode) {
        this.mode = mode;
    }

    @Override
    public T get() {
        switch (mode) {
            case SHARE_VALUE_FOR_ALL_THREADS:
                // overriden case - return not thread local information from superclass variables,
                // but {@link #value}.
                try {
                    lock.lockInterruptibly();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    if (!valueInitialized) {
                        value = initialValue();
                        valueInitialized = true;
                    }
                    return value;
                } finally {
                    lock.unlock();
                }
            case CLASSIC:
            default:
                return super.get();
        }
    }

    @Override
    public void set(T value) {
        switch (mode) {
            case SHARE_VALUE_FOR_ALL_THREADS:
                // overriden case - set not in thread local superclass variable, but to
                // {@link #value}.
                try {
                    lock.lockInterruptibly();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    valueInitialized = true;
                    this.value = value;
                    break;
                } finally {
                    lock.unlock();
                }
            case CLASSIC:
            default:
                super.set(value);
        }
    }


    @Override
    public void remove() {
        switch (mode) {
            case SHARE_VALUE_FOR_ALL_THREADS:
                try {
                    lock.lockInterruptibly();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    valueInitialized = false;
                    this.value = null;
                    break;
                } finally {
                    lock.unlock();
                }
            case CLASSIC:
            default:
                super.remove();
        }
    }

    public static enum ThreadLocalMode {
        /**
         * Forces the upper class to act as a simple {@link ThreadLocal}
         */
        CLASSIC,
        /**
         * Mode to share the single variable value across all threads.
         */
        SHARE_VALUE_FOR_ALL_THREADS
    }

    public static ThreadLocalMode getDefaultModeForThreadSharing() {
        return SHARE_VALUE_FOR_ALL_THREADS;
    }

    public static ThreadLocalMode getDefaultMode(boolean shareAcrossThreads) {
        return shareAcrossThreads ? getDefaultModeForThreadSharing() : CLASSIC;
    }
}
