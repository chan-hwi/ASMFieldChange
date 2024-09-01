# ASMFieldChange
ASMFieldChange is an instrumentation tool for tracking the changes of field values of the classes in patched program. It adds some codes for logging the field change count and history for each assignment of field values.

NOTE: Some parts of its codes such as [Path.java](src/main/java/org/example/utils/Path.java) and [InstrumentClassWriter.java](src/main/java/org/example/asm/InstrumentClassWriter.java) were written with reference to the [JPatchInst repository](https://github.com/UNIST-LOFT/JPatchInst).

## How to use
JDK Version: 1.8

1. Clone repository
```shell
git clone https://github.com/chan-hwi/ASMFieldChange.git
```
2. Build project and run with target patched program
```shell
cd ASMFieldChange
gradlew shadowJar
```

```shell
java -jar build/libs/ASMFieldChange.jar [option] <target_class_path>
```
`target_class_path`: Absolute path to the target patched program classes

### Options
`-t/--time-output-file <file>`: Compute and save the time to instrument each file.

## Running example
Original Target program ([Adder.java](src/main/java/test/Adder.java))

```java
package test;

import java.util.ArrayList;
import java.util.List;

public class Adder {
    private int num;
    private int numSq;
    private final List<Integer> history = new ArrayList<>();

    static int opCount = 0;

    public Adder(int num) {
        this.num = num;
        history.add(num);
    }

    public Adder() {
        this(0);
    }

    public int getNum() {
        return num;
    }

    public List<Integer> getHistory() {
        return this.history;
    }

    public void add(int addend) {
        num += addend;
        numSq = num * num;
        opCount++;

        history.add(num);
    }
}
```

Build and run ASMFieldChange
```shell
java -jar build/libs/ASMFieldChange.jar <Absolute path to "build/classes/java/main/Test">
```

Output

```java
package test;

import java.util.ArrayList;
import java.util.List;

import org.example.runtime.FieldChangeLogger;

public class Adder {
    private int num;
    private int numSq;
    private final List<Integer> history;
    static int opCount = 0;

    public Adder(int num) {
        if (!FieldChangeLogger.isInitialized) {
            FieldChangeLogger.initialize();
        }

        this.history = new ArrayList();
        FieldChangeLogger.logFieldChange(this, "Test.Adder", "history");
        this.num = num;
        FieldChangeLogger.logFieldChange(this, "Test.Adder", "num");
        this.history.add(num);
    }

    public Adder() {
        this(0);
        if (!FieldChangeLogger.isInitialized) {
            FieldChangeLogger.initialize();
        }

    }

    public int getNum() {
        return this.num;
    }

    public List<Integer> getHistory() {
        return this.history;
    }

    public void add(int addend) {
        this.num += addend;
        FieldChangeLogger.logFieldChange(this, "Test.Adder", "num");
        this.numSq = this.num * this.num;
        FieldChangeLogger.logFieldChange(this, "Test.Adder", "numSq");
        ++opCount;
        FieldChangeLogger.logFieldChange((Object) null, "Test.Adder", "opCount");
        this.history.add(this.num);
    }

    static {
        FieldChangeLogger.logFieldChange((Object) null, "Test.Adder", "opCount");
    }
}
```

To get the actual field change logs, you should add compiled [FieldChangeLogger](src/main/java/org/example/runtime/FieldChangeLogger.java) to the class path when execute the instrumented target program. After executing, it generates field_change_history.txt which contains the changelog as belows.

```text
Test.Adder@16b98e56(6)
	numSq(2) = [25, 225]
	num(3) = [0, 5, 15]
	history(1) = [[0, 5, 15]]
Test.Main(3)
	tmp(3) = [0, 1, 2]
Test.Adder(4)
	opCount(4) = [0, 1, 2, 3]
Test.Wrapper@4f3f5b24(4)
	target(4) = [null, null, Test.Adder@16b98e56, Test.Adder@7ef20235]
Test.Adder@7ef20235(4)
	numSq(1) = [25]
	num(2) = [5, -5]
	history(1) = [[5, -5]]
```

Each number wrapped in the parenthesis means the total number of changes of the fields in the instance or the number of changes of each field. The array on the right side of each field contains the changelog.