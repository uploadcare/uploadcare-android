package com.uploadcare.android.widget.utils

/**
 * Reusable SingletonHolder implementation. Double-checked locking algorithm.
 */
open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile
    private var instance: T? = null

    internal open fun init(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }

    open fun getInstance(): T {
        val i = instance
        if (i != null) {
            return i
        }

        throw IllegalStateException("Error project setup is incorrect")
    }
}