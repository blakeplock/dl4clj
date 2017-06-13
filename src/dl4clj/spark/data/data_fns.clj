(ns ^{:doc "multi method and user facing fns for the spark dataset functions

see: https://deeplearning4j.org/doc/org/deeplearning4j/spark/data/package-summary.html"}
    dl4clj.spark.data.data-fns
  (:import [org.deeplearning4j.spark.data
            BatchAndExportMultiDataSetsFunction
            BatchAndExportDataSetsFunction
            PathToMultiDataSetFunction
            MultiDataSetExportFunction
            SplitDataSetsFunction
            PathToDataSetFunction
            DataSetExportFunction
            BatchDataSetsFunction]
           [org.deeplearning4j.spark.data.shuffle SplitDataSetExamplesPairFlatMapFunction])
  (:require [dl4clj.utils :refer [generic-dispatching-fn]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; multi method for constructor calling
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti ds-fns generic-dispatching-fn)

(defmethod ds-fns :batch-and-export-ds [opts]
  (let [conf (:batch-and-export-ds opts)
        {batch-size :batch-size
         path :export-path} conf]
    (BatchAndExportDataSetsFunction. batch-size path)))

(defmethod ds-fns :batch-and-export-multi-ds [opts]
  (let [conf (:batch-and-export-multi-ds opts)
        {batch-size :batch-size
         path :export-path} conf]
    (BatchAndExportMultiDataSetsFunction. batch-size path)))

(defmethod ds-fns :batch-ds [opts]
  (let [mbs (:batch-size (:batch-ds opts))]
    (BatchDataSetsFunction. mbs)))

;; did i miss batch-multi?????

(defmethod ds-fns :export-ds [opts]
  (let [p (:export-path (:export-ds opts))]
    (DataSetExportFunction. (java.net.URI/create p))))

(defmethod ds-fns :export-multi-ds [opts]
  (let [p (:export-path (:export-multi-ds opts))]
    (MultiDataSetExportFunction. (java.net.URI/create p))))

(defmethod ds-fns :split-ds [opts]
  (SplitDataSetsFunction.))

(defmethod ds-fns :split-ds-rand [opts]
  (let [max-k (:max-key-idx (:split-ds-rand opts))]
    (SplitDataSetExamplesPairFlatMapFunction. max-k)))

(defmethod ds-fns :path-to-ds [opts]
  (PathToDataSetFunction.))

(defmethod ds-fns :path-to-multi-ds [opts]
  (PathToMultiDataSetFunction.))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; user facing fns
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn new-batch-and-export-ds-fn
  "Function used with (map-partition-with-idx RDD<DataSet>)
    - not yet implemented at time of writing doc string

  It does two things:
  1. Batch DataSets together, to the specified minibatch size.
     This may result in splitting or combining existing DataSet objects as required
  2. Export the DataSet objects to the specified directory.

  Naming convention for export files: (str dataset_ partition-idx jvm_uid _ idx .bin)

  :batch-size (int), the size of the batches

  :export-path (str), the directory you want to export to"
  [& {:keys [batch-size export-path]
      :as opts}]
  (ds-fns {:batch-and-export-ds opts}))

(defn new-batch-and-export-multi-ds-fn
  "Function used with (map-partition-with-idx RDD<MultiDataSet>)
   - not yet implemented at time of writing doc string

  It does two things:
  1. Batch MultiDataSets together, to the specified minibatch size.
     This may result in splitting or combining existing MultiDataSet objects as required
  2. Export the MultiDataSet objects to the specified directory.

  Naming convention for export files: (str mds_ partition-idx jvm_uid _ idx .bin)

  :batch-size (int), the size of the batches

  :export-path (str), the directory you want to export to"
  [& {:keys [batch-size export-path]
      :as opts}]
  (ds-fns {:batch-and-export-multi-ds opts}))

(defn new-batch-ds-fn
  "Function used to batch DataSet objects together.

  Typically used to combine singe-example DataSet objects out of something
  like DataVecDataSetFunction together into minibatches.

  Usage: (def single-ds-ex (map-partitions (new-batch-ds-fn n)))

  batch-size (int), the size of the batches"
  [& {:keys [batch-size]
      :as opts}]
  (ds-fns {:batch-ds opts}))

(defn new-ds-export-fn
  "A function to save DataSet objects to disk/HDFS.
   - used with (for-each-partition [data JavaRDD<DataSet>] (new-ds-export-fn export-path))
     - need to implement for-each-partition at time of writing doc string

  Each DataSet object is given a random and (probably) unique name starting with
  dataset_ and ending with .bin.

  export-path (str), the place to export to"
  [& {:keys [export-path]
      :as opts}]
  (ds-fns {:export-ds opts}))

(defn new-multi-ds-export-fn
  "A function to save MultiDataSet objects to disk/HDFS.
   - used with (for-each-partition [data JavaRDD<MultiDataSet>] (new-ds-export-fn export-path))
     - need to implement for-each-partition at time of writing doc string

  Each MultiDataSet object is given a random and (probably) unique name starting with
  dataset_ and ending with .bin.

  export-path (str), the place to export to"
  [& {:keys [export-path]
      :as opts}]
  (ds-fns {:export-multi-ds opts}))

(defn new-path-to-ds-fn
  "Simple function used to load DataSets from a given Path (str) to a DataSet object
    - (serialized with (save-ds DataSet))
    - not yet implemented at time of writing doc string
   - i.e., RDD<String> to RDD<DataSet>"
  []
  (ds-fns {:path-to-ds {}}))

(defn new-path-to-multi-ds-fn
  "Simple function used to load MultiDataSets from a given Path (str) to a MultiDataSet object
    - (serialized with (save-ds MultiDataSet))
    - not yet implemented at time of writing doc string
   - i.e., RDD<String> to RDD<MultiDataSet>"
  []
  (ds-fns {:path-to-multi-ds {}}))

(defn new-split-ds-fn
  "Take an existing DataSet object, and split it into multiple DataSet objects
  with one example in each"
  []
  (ds-fns {:split-ds {}}))

(defn new-split-ds-with-appended-key
  "splits each example in a DataSet object into its own DataSet.

  Also adds a random key (int) in the range 0 to (- max-key-idx 1).

  max-key-idx (int), used for adding random keys to the new datasets"
  [& {:keys [max-key-idx]
      :as opts}]
  (ds-fns {:split-ds-rand opts}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; calling fns
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn call-batch-and-export-ds-fn!
  "uses the object created by new-batch-and-export-ds-fn
   to perform its function
    - this fn has the ability to create the object and call it
      in a single use when you provide a config map for the
      batch-and-export-ds-fn

  :the-fn (obj or config-map), will accept the object created using
   new-batch-and-export-ds-fn, but will also accept a config map used
   to call the ds-fns multimethod directly
    - config map = {:batch-and-export-ds opts}
      - opts is a map with keys/values described in new-batch-and-export-ds-fn

  :partition-idx (int), tag for labeling the new datasets created via this fn

  :ds-iter (dataset iterator), the iterator which goes through a dataset
   - see: dl4clj.datasets.datavec, dl4clj.datasets.iterator.iterators
   - NOTE: make sure to reset the iter after uses"
  [& {:keys [the-fn partition-idx ds-iter]}]
  (if (map? the-fn)
    (.call (ds-fns the-fn) (int partition-idx) ds-iter)
    (.call the-fn (int partition-idx) ds-iter)))

(defn call-batch-and-export-multi-ds-fn!
  "uses the object created by new-batch-and-export-multi-ds-fn
   to perform its function
    - this fn has the ability to create the object and call it
      in a single use when you provide a config map for the
      batch-and-export-multi-ds-fn

  :the-fn (obj or config-map), will accept the object created using
   new-batch-and-export-ds-fn, but will also accept a config map used
   to call the ds-fns multimethod directly
    - config map = {:batch-and-export-multi-ds opts}
      - opts is a map with keys/values described in new-batch-and-export-multi-ds-fn

  :partition-idx (int), tag for labeling the new datasets created via this fn

  :multi-ds-iter (multi-dataset iterator), the iterator which goes through a dataset
   - see: dl4clj.datasets.datavec, dl4clj.datasets.iterator.iterators
   - NOTE: make sure to reset the iter after uses"
  [& {:keys [the-fn partition-idx multi-ds-iter]}]
  (if (map? the-fn)
    (.call (ds-fns the-fn) (int partition-idx) multi-ds-iter)
    (.call the-fn (int partition-idx) multi-ds-iter)))

(defn call-batch-ds-fn!
  ;; will write this doc tomorrow
  ""
  [& {:keys [the-fn ds-iter]}]
  (if (map? the-fn)
    (.call (ds-fns the-fn) ds-iter)
    (.call the-fn ds-iter)))
