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
Original Target program ([Adder.java](src/main/java/Test/Adder.java))
```java
package Test;

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
package Test;

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
        this.num = num;
        this.history.add(num);
        FieldChangeLogger.logFieldChange(0, this, "Test.Adder");
    }

    public Adder() {
        this(0);
        if (!FieldChangeLogger.isInitialized) {
            FieldChangeLogger.initialize();
        }

        FieldChangeLogger.logFieldChange(1, this, "Test.Adder");
    }

    public int getNum() {
        int var10000 = this.num;
        FieldChangeLogger.logFieldChange(2, this, "Test.Adder");
        return var10000;
    }

    public List<Integer> getHistory() {
        List var10000 = this.history;
        FieldChangeLogger.logFieldChange(3, this, "Test.Adder");
        return var10000;
    }

    public void add(int addend) {
        this.num += addend;
        this.numSq = this.num * this.num;
        ++opCount;
        this.history.add(this.num);
        FieldChangeLogger.logFieldChange(4, this, "Test.Adder");
    }

    static {
        FieldChangeLogger.logFieldChange(5, (Object)null, "Test.Adder");
    }
}
```

To get the actual field change logs, you should add compiled [FieldChangeLogger](src/main/java/org/example/runtime/FieldChangeLogger.java) and dependencies (xstream-1.4.20.jar and xmlpull-1.1.3.1.jar) to the class path when execute the instrumented target program. After executing, it generates field_change_history.txt which contains the field valuese of the class at each branch formatted by xstream.

```XML
<map>
  <entry>
    <string>Test.Main</string>
    <map>
      <entry>
        <string>Test.Main.5</string>
        <list>
          <map>
            <entry>
              <string>tmp</string>
              <int>2</int>
            </entry>
          </map>
        </list>
      </entry>
      <entry>
        <string>Test.Main.6</string>
        <list>
          <map>
            <entry>
              <string>tmp</string>
              <int>0</int>
            </entry>
          </map>
        </list>
      </entry>
...
```
The structure of the above xml is as follows.

```XML
<map> <!-- HashMap containing field values of all classes at each branch -->
  <entry> <!-- class -->
    <string>{classname}</string>
    <map> <!-- Hashmap containing field values at each branch in the class -->
      <entry> <!-- branch -->
        <string>{classname}.{branchId}</string>
        <list> <!-- List containing the history of field values at the branch on each execution -->
          <map> <!-- Hashmap containing the actual field values -->
            <entry>
              <string>{field 1}</string>
              <int>{value}</int>
            </entry>
            <entry>
              <string>{field 2}</string>
              <list>
                <int>{value 1}</int>
                <int>{value 2}</int>
              </list>
            </entry>
            ...
          </map>
          <map>
            <entry>
              <string>{field 1}</string>
              <int>{updated value}</int>
            </entry>
            <entry>
              <string>{field 2}</string>
              <list>
                <int>{updated value 1}</int>
                <int>{updated value 2}</int>
              </list>
            </entry>
            ...
          </map>
        </list>
      </entry>
    </map>
  </entry>
</map>
```
