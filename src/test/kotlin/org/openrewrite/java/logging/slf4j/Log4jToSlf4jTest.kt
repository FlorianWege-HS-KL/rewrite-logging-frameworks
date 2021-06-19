/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.logging.slf4j

import org.junit.jupiter.api.Test
import org.openrewrite.Recipe
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest

class Log4jToSlf4jTest : JavaRecipeTest {
    override val parser: JavaParser = JavaParser.fromJavaVersion()
        .logCompilationWarningsAndErrors(true)
        .classpath("log4j")
        .build()

    override val recipe: Recipe
        get() = Log4jToSlf4j()

    @Test
    fun loggingStatements() = assertChanged(
        before = """
            import org.apache.log4j.Logger;

            class A {
                Logger logger = Logger.getLogger(A.class);

                void myMethod() {
                    String name = "Jon";
                    logger.fatal("uh oh");
                    logger.info(new Object());
                    logger.info("Hello " + name + ", nice to meet you " + name);
                }
            }
        """,
        after = """
            import org.slf4j.Logger;
            import org.slf4j.LoggerFactory;

            class A {
                Logger logger = LoggerFactory.getLogger(A.class);

                void myMethod() {
                    String name = "Jon";
                    logger.error("uh oh");
                    logger.info(new Object().toString());
                    logger.info("Hello {}, nice to meet you {}", name, name);
                }
            }
        """,
        skipEnhancedTypeValidation = true // fixme
    )

}