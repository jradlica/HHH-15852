/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 * <p>
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

    private static final Logger log = LoggerFactory.getLogger(ORMUnitTestCase.class);

    public static final String TYPE_A = "Type-A";
    public static final String TYPE_B = "Type-B";
    public static final byte[] byteArrayOfSize1MB = readRandomByteArrayOfSize1MB();

    // Add your entities here.

    @Override
    protected Class[] getAnnotatedClasses() {
        return new Class[]{
                TestEntity.class,
                AnotherEntity.class,
        };
    }

    @Override
    protected void configure(Configuration configuration) {
        super.configure(configuration);

        configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString());
        configuration.setProperty(AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString());
    }

    // Add your tests, using standard JUnit.

    @Test
    public void hhh15852Test() throws Exception {
        persit300Entities();

        Session session = openSession();

        Query query = session.createQuery("SELECT te from TestEntity te JOIN FETCH te.relatedEntities ae WHERE ae.type='Type-A'");
        query.setFirstResult(0);
        query.setMaxResults(5);

        // The below statement will throw OutOfMemoryException
        List list = query.list();
        /** The OutOfMemoryException should being thrown, if not, please remember to set `-Xmx256M`
        * Only 5 entities should be loaded into memory. However in the {@link  QuerySqmImpl#doList()}
        * at the line 546: final List<R> list = resolveSelectQueryPlan().performList( executionContextToUse ); the all entities are loaded
        **/
    }

    @Test //The above scenario works for the regular join
    public void regularJoinTest() throws Exception {
        persit300Entities();

        Session session = openSession();

        Query query = session.createQuery("SELECT te from TestEntity te JOIN te.relatedEntities ae WHERE ae.type='Type-A'");
        query.setFirstResult(0);
        query.setMaxResults(5);

        //The below statement will not throw OufOfMemoryException because only 5 results will be loaded into memory
        List list = query.list();
    }

    private void persit300Entities() {
        for (int i = 0; i < 6; i++) {
            Session s = openSession();
            Transaction tx = s.beginTransaction();
            for (int j = 0; j < 50; j++) {
                TestEntity testEntity = newEntityWithRandomValues();
                s.persist(testEntity);
                log.info("Persisted: " + (i*50+j) +" / " + (300));
                s.flush();
                s.clear();
            }
            tx.commit();
            s.close();
            System.gc();
        }
    }

    private TestEntity newEntityWithRandomValues() {
        TestEntity testEntity = new TestEntity(
                UUID.randomUUID().toString()
        );
        AnotherEntity anotherEntity = new AnotherEntity(
                testEntity,
                randomType(),
                byteArrayOfSize1MB
        );

        testEntity.addRelatedEntity(anotherEntity);

        return testEntity;
    }

    private static String randomType() {
        return ThreadLocalRandom.current().nextBoolean() ? TYPE_A : TYPE_B;
    }

    private static byte[] readRandomByteArrayOfSize1MB() {
        try {
            return Files.readAllBytes(Path.of("src/test/resources/1MB.file"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
