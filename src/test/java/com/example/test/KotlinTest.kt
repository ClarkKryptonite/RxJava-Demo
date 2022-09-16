package com.example.test

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.jupiter.api.Test

class KotlinTest : BaseTest() {
    /**
     * ```console
    doOnNext thread:RxCachedThreadScheduler-1 value:1
    doOnNext thread:RxCachedThreadScheduler-1 value:2
    onNext thread:RxSingleScheduler-1 value:1
    doOnNext thread:RxCachedThreadScheduler-1 value:3
    onNext thread:RxSingleScheduler-1 value:2
    onNext thread:RxSingleScheduler-1 value:3
    doOnNext thread:RxCachedThreadScheduler-1 value:4
    onNext thread:RxSingleScheduler-1 value:4
     * ```
     */
    @Test
    fun testDoOnNext() {
        Observable.just(1, 2, 3, 4)
                .doOnNext { println("doOnNext thread:${Thread.currentThread().name} value:$it") }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe { println("onNext thread:${Thread.currentThread().name} value:$it") }
    }

    /**
     * ```console
     * num:4
     * onNext it:4
     * else if num
     * println text num == 4
     * BUILD SUCCESSFUL in 13s
     * ```
     */
    @Test
    fun test() {
        val num = 4
        println("num:$num")
        if (num < 4) {
            Observable.range(0, 3)
        } else if (num == 4) {
            Observable.zip(
                    Observable.just(4),
                    Observable.range(1, 4)
            ) { num1, _ ->
                return@zip num1
            }
        } else {
            Observable.range(5, 5)
        }
                .subscribe {
                    println("onNext it:$it")
                }

        if (num < 4) {
            println("if num")
            "text 3"
        } else if (num <= 5) {
            println("else if num")
            "text num == 4"
        } else {
            println("else num")
            "text num > 4"
        }.let {
            println("println $it")
        }
    }

    /**
     * ```console
    onError:error 1111
    io.reactivex.rxjava3.exceptions.UndeliverableException: The exception could not be delivered to the consumer because it has already canceled/disposed the flow or the exception has nowhere to go to begin with. Further reading: https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling | java.lang.Throwable: error 2222
    at io.reactivex.rxjava3.plugins.RxJavaPlugins.onError(RxJavaPlugins.java:372)
    at io.reactivex.rxjava3.internal.operators.observable.ObservableCreate$CreateEmitter.onError(ObservableCreate.java:74)
    at com.example.test.KotlinTest.testOnError$lambda-5(KotlinTest.kt:76)
    at io.reactivex.rxjava3.internal.operators.observable.ObservableCreate.subscribeActual(ObservableCreate.java:41)
    at io.reactivex.rxjava3.core.Observable.subscribe(Observable.java:13176)
    at io.reactivex.rxjava3.core.Observable.subscribe(Observable.java:13121)
    at com.example.test.KotlinTest.testOnError(KotlinTest.kt:77)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.base/java.lang.reflect.Method.invoke(Method.java:566)
    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:688)
    at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
    at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
    at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
    at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestableMethod(TimeoutExtension.java:140)
    at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestMethod(TimeoutExtension.java:84)
    at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
    at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
    at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:210)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:206)
    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:65)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:129)
    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:127)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:126)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:84)
    at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:143)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:129)
    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:127)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:126)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:84)
    at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:143)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:129)
    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:127)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:126)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:84)
    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)
    at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
    at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)
    at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:108)
    at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:88)
    at org.junit.platform.launcher.core.EngineExecutionOrchestrator.lambda$execute$0(EngineExecutionOrchestrator.java:54)
    at org.junit.platform.launcher.core.EngineExecutionOrchestrator.withInterceptedStreams(EngineExecutionOrchestrator.java:67)
    at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:52)
    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:96)
    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:75)
    at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor$CollectAllTestClassesExecutor.processAllTestClasses(JUnitPlatformTestClassProcessor.java:99)
    at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor$CollectAllTestClassesExecutor.access$000(JUnitPlatformTestClassProcessor.java:79)
    at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor.stop(JUnitPlatformTestClassProcessor.java:75)
    at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.stop(SuiteTestClassProcessor.java:61)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.base/java.lang.reflect.Method.invoke(Method.java:566)
    at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)
    at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.internal.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:33)
    at org.gradle.internal.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:94)
    at com.sun.proxy.$Proxy2.stop(Unknown Source)
    at org.gradle.api.internal.tasks.testing.worker.TestWorker.stop(TestWorker.java:133)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.base/java.lang.reflect.Method.invoke(Method.java:566)
    at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)
    at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:182)
    at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:164)
    at org.gradle.internal.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:414)
    at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
    at org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:48)
    at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
    at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
    at org.gradle.internal.concurrent.ThreadFactoryImpl$ManagedThreadRunnable.run(ThreadFactoryImpl.java:56)
    at java.base/java.lang.Thread.run(Thread.java:829)
    Caused by: java.lang.Throwable: error 2222
    ... 95 more
    Exception in thread "Test worker" io.reactivex.rxjava3.exceptions.UndeliverableException: The exception could not be delivered to the consumer because it has already canceled/disposed the flow or the exception has nowhere to go to begin with. Further reading: https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling | java.lang.Throwable: error 2222
    at io.reactivex.rxjava3.plugins.RxJavaPlugins.onError(RxJavaPlugins.java:372)
    at io.reactivex.rxjava3.internal.operators.observable.ObservableCreate$CreateEmitter.onError(ObservableCreate.java:74)
    at com.example.test.KotlinTest.testOnError$lambda-5(KotlinTest.kt:76)
    at io.reactivex.rxjava3.internal.operators.observable.ObservableCreate.subscribeActual(ObservableCreate.java:41)
    at io.reactivex.rxjava3.core.Observable.subscribe(Observable.java:13176)
    at io.reactivex.rxjava3.core.Observable.subscribe(Observable.java:13121)
    at com.example.test.KotlinTest.testOnError(KotlinTest.kt:77)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.base/java.lang.reflect.Method.invoke(Method.java:566)
    at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:688)
    at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
    at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
    at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
    at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestableMethod(TimeoutExtension.java:140)
    at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestMethod(TimeoutExtension.java:84)
    at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
    at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
    at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
    at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
    at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:210)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:206)
    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
    at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:65)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:139)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:129)
    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:127)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:126)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:84)
    at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:143)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:129)
    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:127)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:126)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:84)
    at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:38)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$5(NodeTestTask.java:143)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$7(NodeTestTask.java:129)
    at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:127)
    at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:126)
    at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:84)
    at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:32)
    at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
    at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:51)
    at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:108)
    at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:88)
    at org.junit.platform.launcher.core.EngineExecutionOrchestrator.lambda$execute$0(EngineExecutionOrchestrator.java:54)
    at org.junit.platform.launcher.core.EngineExecutionOrchestrator.withInterceptedStreams(EngineExecutionOrchestrator.java:67)
    at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:52)
    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:96)
    at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:75)
    at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor$CollectAllTestClassesExecutor.processAllTestClasses(JUnitPlatformTestClassProcessor.java:99)
    at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor$CollectAllTestClassesExecutor.access$000(JUnitPlatformTestClassProcessor.java:79)
    at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor.stop(JUnitPlatformTestClassProcessor.java:75)
    at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.stop(SuiteTestClassProcessor.java:61)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.base/java.lang.reflect.Method.invoke(Method.java:566)
    at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)
    at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.internal.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:33)
    at org.gradle.internal.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:94)
    at com.sun.proxy.$Proxy2.stop(Unknown Source)
    at org.gradle.api.internal.tasks.testing.worker.TestWorker.stop(TestWorker.java:133)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.base/java.lang.reflect.Method.invoke(Method.java:566)
    at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)
    at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:182)
    at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:164)
    at org.gradle.internal.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:414)
    at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
    at org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:48)
    at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
    at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
    at org.gradle.internal.concurrent.ThreadFactoryImpl$ManagedThreadRunnable.run(ThreadFactoryImpl.java:56)
    at java.base/java.lang.Thread.run(Thread.java:829)
    Caused by: java.lang.Throwable: error 2222
    ... 95 more
     * ```
     */
    @Test
    fun testOnMultiError() {
        Observable.create<String> {
            it.onError(Throwable("error 1111"))
            it.onError(Throwable("error 2222"))
        }.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it.message}")
        }, {
            println("onComplete")
        })
    }

    /**
     * ```
    it.onError(Throwable("error 2222"))
     * ```
     */
    @Test
    fun testOnErrorOnComplete() {
        Observable.create<String> {
            it.onError(Throwable("error 1111"))
            it.onComplete()
        }.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it.message}")
        }, {
            println("onComplete")
        })
    }

    /**
     * ```
    onNext:0
    onNext:1
    onNext:1234
    onNext:2345
    onNext:9999
     * ```
     */
    @Test
    fun testNestedError() {
        Observable.create<String> { emitter ->
            emitter.onNext("0")
            emitter.onNext("1")
            Observable.create<Int> {
                it.onNext(1234)
                it.onNext(2345)
                it.onError(Throwable("xiajibace"))
            }.onErrorReturn {
                9999
            }.subscribe({
                emitter.onNext(it.toString())
            }, {
                emitter.onNext("inner emitter error")
                emitter.onError(it)
            })
        }.onErrorReturn {
            "raw emitter error"
        }.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it.message}")
        })
    }

    /**
     * ```
    onNext:10000000
    onNext:20000000
     * ```
     */
    @Test
    fun testOnNextNull() {
        Observable.create<Int> {
            it.onNext(1)
            it.onNext(2)
            it.onNext(3)
        }.map {
            var value: String? = null
            if (it != 3) {
                value = it.toString()
            }
            value!!.length
            value!!
        }.map{
            it + "0000000"
        }.subscribe({
            println("onNext:$it")
        }, {

        })
    }

    /**
     * ```
    onNext:1
    onNext:2
     * ```
     */
    @Test
    fun testFlatMapNull() {
        Observable.create<Int> {
            it.onNext(1)
            it.onNext(2)
        }.flatMap {
            Observable.create<String> { emitter->
                emitter.onNext(it.toString())
            }
        }.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it.message}")
        })
    }

    /**
     * ```
    hahaha
    onNext:kotlin.Unit
     * ```
     */
    @Test
    fun testUnitFunction() {
        Observable.just(1).map {
            printUnitFunction()
        }.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it.message}")
        })
    }

    private fun printUnitFunction() {
        println("hahaha")
    }

    /**
     * ```
     ...
    Caused by: java.lang.NullPointerException: The mapper function returned a null value.
    at java.base/java.util.Objects.requireNonNull(Objects.java:246)
    at io.reactivex.rxjava3.internal.operators.observable.ObservableMap$MapObserver.onNext(ObservableMap.java:58)
    ... 98 more
     * ```
     */
    @Test
    fun testMapNull() {
        Observable.just(1).map {
            null
        }.subscribeNextAndError()
    }

    /**
     * ```shell
    1 onNext 0
    2 onNext
    subscribe:0
    2 onComplete
    1 onComplete
     * ```
     */
    @Test
    fun testFlatMapComplete() {
        Observable.create<Int> {
            println("1 onNext 0")
            it.onNext(0)
            println("1 onComplete")
            it.onComplete()
        }.flatMap { value ->
            Observable.create<String> {
                println("2 onNext")
                it.onNext(value.toString())
                println("2 onComplete")
                it.onComplete()
            }
        }.subscribe({
            println("subscribe:$it")
        }, {

        })
    }
}