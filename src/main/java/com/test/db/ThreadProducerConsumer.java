package com.test.db;

import com.test.db.model.Historical;

import java.util.*;

public class ThreadProducerConsumer {

    private static final int ENTITY_COUNT = 30000;
    private static final int CURRENT_SAMPLES_COUNT = 10;
    private List<Historical> historicalList;
    private DatabaseManager databaseManager;

    public ThreadProducerConsumer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.historicalList = getHistoricalList();
    }

    public void startPcThread() throws InterruptedException {
        ProducerConsumer pc = new ProducerConsumer();

        Thread producerThread = new Thread(() -> {
            try {
                pc.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumerThread = new Thread(() -> {
            try {
                pc.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();
    }

    public class ProducerConsumer {
        int queryCount = 0;
        List<Historical> forInsertList = new ArrayList<>();

        public void produce() throws InterruptedException {
            while (true) {
                synchronized (this) {
                    while (queryCount == 1) {
                        wait();
                    }
                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            for (int i = 1; i <= 20; i++) {
                                for (int j = 0; j < CURRENT_SAMPLES_COUNT; j++) {
                                    Calendar instance = Calendar.getInstance();
                                    instance.setTime(new Date());
                                    instance.add(Calendar.DATE, i);
                                    if (!historicalList.isEmpty()) {
                                        Historical historical = historicalList.get(j);
                                        historical.setStepId(i);
                                        historical.setStepBegin(new Date());
                                        historical.setStepEnd(instance.getTime());
                                        forInsertList.add(Historical.builder()
                                                .laboratoryId(historicalList.get(j).getLaboratoryId())
                                                .sampleId(historicalList.get(j).getSampleId())
                                                .stepId(i)
                                                .stepBegin(new Date())
                                                .stepEnd(instance.getTime())
                                                .build());
                                    }
                                }
                            }
                            insert(forInsertList);
                            forInsertList.clear();
                            removeFirstElementsFromList();
                        }
                    };

                    timer.schedule(timerTask, new Date(), 10000);

                    queryCount++;
                    notify();
                    Thread.sleep(120000);
                    timer.cancel();
                }
            }
        }

        public void consume() throws InterruptedException {
            while (true) {
                synchronized (this) {
                    while (queryCount == 0) {
                        wait();
                    }
                    selectHistoric();
                    queryCount--;
                    notify();
                }
            }
        }

    }

    private List<Historical> getHistoricalList() {
        List<Historical> historicalList = new ArrayList<>();
        for (int i = 1; i <= ENTITY_COUNT; i++) {
            historicalList.add(Historical.builder()
                    .laboratoryId(1)
                    .sampleId(i)
                    .build());
        }

        return historicalList;
    }

    private void removeFirstElementsFromList() {
        Iterator<Historical> iterator = historicalList.iterator();
        int sampleNumber = 0;
        while (sampleNumber < CURRENT_SAMPLES_COUNT) {
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
                sampleNumber++;
            }
        }
    }

    public void insert(List<Historical> historicals) {
        databaseManager.insert(historicals);
    }

    public void selectHistoric() {
        databaseManager.selectHistoric();
    }

    public List<Historical> select(Date date) {
        return databaseManager.select(date);
    }

    public List<Historical> selectAll() {
        return databaseManager.selectAll();
    }

    public void deleteAll() {
        databaseManager.deleteAll();
    }

    public void test(Historical historical) {
        System.out.println(historical);
    }
}
