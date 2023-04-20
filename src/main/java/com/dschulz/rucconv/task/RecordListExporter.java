package com.dschulz.rucconv.task;

import java.util.List;

public interface RecordListExporter<T> extends AutoCloseable{
    public void export(List<T> lista);
}
