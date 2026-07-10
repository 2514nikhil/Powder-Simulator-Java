module powdersimulator {
    requires java.base;
    requires javafx.controls;
    requires transitive javafx.graphics;

    exports com.powdersimulator;
    opens com.powdersimulator to javafx.graphics;
}