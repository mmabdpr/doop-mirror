package org.clyze.doop.wala;

import com.ibm.wala.classLoader.IClass;
import soot.SootMethod;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WalaDriver {
    private WalaThreadFactory _factory;

    private ExecutorService _executor;
    private int _classCounter;
    private Set<IClass> _tmpClassGroup;
    private int _totalClasses;
    private int _classSplit = 80;

    WalaDriver(WalaThreadFactory factory, int totalClasses, Integer cores) {
        _factory = factory;
        _classCounter = 0;
        _tmpClassGroup = new HashSet<>();
        _totalClasses = totalClasses;
        int _cores = cores == null? Runtime.getRuntime().availableProcessors() : cores;

        System.out.println("Fact generation cores: " + _cores);

        if (_cores > 2) {
            _executor = new ThreadPoolExecutor(_cores /2, _cores, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        } else {
            _executor = new ThreadPoolExecutor(1, _cores, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        }
    }

    void doInParallel(Iterator<IClass> classesToProcess) {
       while (classesToProcess.hasNext()) {
            generate(classesToProcess.next());
        }
    }

    void writeInParallel(Set<IClass> classesToProcess) {
        classesToProcess.forEach(this::write);

    }

    void shutdown() {
        _executor.shutdown();
        try {
            _executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    void doAndroidInSequentialOrder(SootMethod dummyMain, Set<IClass> iClasses, WalaFactWriter writer, boolean ssa) {
        WalaFactGenerator factGenerator = new WalaFactGenerator(writer, iClasses);
        //factGenerator.generate(dummyMain, new Session());
        //writer.writeAndroidEntryPoint(dummyMain);
        factGenerator.run();
    }

    private void generate(IClass curClass) {
        _classCounter++;
        _tmpClassGroup.add(curClass);

        if ((_classCounter % _classSplit == 0) || (_classCounter == _totalClasses)) {
            Runnable runnable = _factory.newFactGenRunnable(_tmpClassGroup);
            _executor.execute(runnable);
            _tmpClassGroup = new HashSet<>();
        }
    }

    private void write(IClass curClass) {
//        _classCounter++;
//        _tmpClassGroup.add(curClass);
//
//        if ((_classCounter % _classSplit == 0) || (_classCounter == _totalClasses)) {
//            Runnable runnable = _factory.newJimpleGenRunnable(_tmpClassGroup);
//            _executor.execute(runnable);
//            _tmpClassGroup = new HashSet<>();
//        }
    }

}
