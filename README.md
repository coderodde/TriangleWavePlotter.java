# TriangleWavePlotter.java
An exercise app in Java for interfacing with Gnuplot to plot triangle waves.

# Requirements
You need to have `gnuplot` on your `PATH` environment variable.

# Compiling and running
```
git clone https://github.com/coderodde/TriangleWavePlotter.java.git
cd TriangleWAavePlotter.java
mvn clean compile jar:jar
cd target
java -jar TriangleWavePlotter-1.6.jar --help
java -jar TriangleWavePlotter-1.6.jar --amplitude=2 --period=4 --shift=-1 --xRangeStart=-12 --xRangeEnd=12 --yRangeStart=-3 --yRangeEnd=3 %USERPROFILE%/OneDrive/Desktop/TriangleWave.png
```

The last command will give you:
![TriangleWave](https://github.com/coderodde/TriangleWavePlotter.java/assets/1770505/18f1b685-f20b-439b-aaed-3528c21e9228)
