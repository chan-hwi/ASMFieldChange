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

    public int getNumSq() {
        return numSq;
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
        FieldChangeLogger.logFieldChange(this, "Test.Adder");
        this.num = num;
        FieldChangeLogger.logFieldChange(this, "Test.Adder");
        this.history.add(num);
    }

    public Adder() {
        this(0);
        if (!FieldChangeLogger.isInitialized) {
            FieldChangeLogger.initialize();
        }
    }

    public int getNum() {
        int var10000 = this.num;
        FieldChangeLogger.logFieldChange(this, "Test.Adder");
        return var10000;
    }

    public int getNumSq() {
        int var10000 = this.numSq;
        FieldChangeLogger.logFieldChange(this, "Test.Adder");
        return var10000;
    }

    public List<Integer> getHistory() {
        List var10000 = this.history;
        FieldChangeLogger.logFieldChange(this, "Test.Adder");
        return var10000;
    }

    public void add(int addend) {
        this.num += addend;
        this.numSq = this.num * this.num;
        ++opCount;
        this.history.add(this.num);
        FieldChangeLogger.logFieldChange(this, "Test.Adder");
    }

    static {
        FieldChangeLogger.logFieldChange((Object)null, "Test.Adder");
    }
}
```

To get the actual field change logs, you should add compiled [src/main/java/org/example/runtime](src/main/java/org/example/runtime) and dependencies (xstream-1.4.20.jar and xmlpull-1.1.3.1.jar) to the class path when execute the instrumented target program. After executing, it generates two files in the source root as belows.
- `field_change_history.xml` - contains the changelog of field values in XML format.
- `field_change_hash.xml` - contains the hashcode of the changelog of field values in XML format.

You can see the sample of each file in [logs](logs) directory.

## How to utilize the output?
The `field_change_history.xml` file is primarily used for debugging purposes and is not suited for Greybox analysis due to its high memory consumption. In contrast, the `field_change_hash.xml` file is more memory-efficient, as it only stores the hashes of field changelogs. By comparing these hashes, critical fields can be identified with significantly less overhead.
