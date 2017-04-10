# dl4clj

Port of [deeplearning4j](https://github.com/deeplearning4j/) to clojure

## Usage

Under construction. For now, have a look at the [examples](https://github.com/engagor/dl4clj/tree/master/src/dl4clj/examples) to get started.

## Artifacts

dl4clj artifacts are released to Clojars.

If using Maven add the following repository definition to your pom.xml:

```
<repository>
  <id>clojars.org</id>
  <url>http://clojars.org/repo</url>
</repository>
```

## Latest release

With Leiningen:

```
[engagor/clj-vw "0.0.1"]
```

With Maven:

```
<dependency>
  <groupId>engagor</groupId>
  <artifactId>dl4clj</artifactId>
  <version>0.0.1</version>
</dependency>
```

# TODO

Implement ComputationGraphs
<https://deeplearning4j.org/doc/org/deeplearning4j/nn/conf/ComputationGraphConfiguration.GraphBuilder.html>
<https://deeplearning4j.org/doc/org/deeplearning4j/nn/conf/ComputationGraphConfiguration.html>
<https://deeplearning4j.org/doc/org/deeplearning4j/nn/conf/graph/package-frame.html>
<https://deeplearning4j.org/doc/org/deeplearning4j/nn/conf/graph/rnn/package-frame.html>

Finish Variational layers (Autoencoders)
<https://deeplearning4j.org/doc/org/deeplearning4j/nn/conf/layers/variational/package-frame.html>
## License

Copyright © 2016 Engagor

Distributed under the BSD Clause-2 License as distributed in the file LICENSE at the root of this repository.
