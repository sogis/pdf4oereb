$GRAALVM_HOME/bin/native-image --verbose --report-unsupported-elements-at-runtime --allow-incomplete-classpath -H:-UseServiceLoaderFeature -H:+ReportExceptionStackTraces --no-server -cp "lib/*" ch.so.agi.oereb.pdf4oereb.Main

$GRAALVM_HOME/bin/native-image --verbose --allow-incomplete-classpath -H:+ReportExceptionStackTraces -H:-UseServiceLoaderFeature -H:IncludeResources='.*/*.xslt$'  -H:Log=registerResource --no-server -cp "lib/*" ch.so.agi.oereb.pdf4oereb.Main

$GRAALVM_HOME/bin/native-image --verbose --allow-incomplete-classpath --report-unsupported-elements-at-runtime  --delay-class-initialization-to-runtime=org.apache.batik.bridge.RhinoInterpreter -H:+ReportExceptionStackTraces -H:-UseServiceLoaderFeature -H:ReflectionConfigurationFiles=../../../../graalvm/reflection.json -H:IncludeResources='.*/*.xslt$' -H:IncludeResources='.*/*.xconf$' -H:IncludeResources='.*/*.ttf$' -H:IncludeResources='.*/*.resx$' -H:Log=registerResource --no-server -cp "lib/*" ch.so.agi.oereb.pdf4oereb.Main


org.apache.http.ssl.SSLInitializationException: TLS SSLContext not available
-> security.provider.3=sun.security.ec.SunEC wieder entkommentiert.