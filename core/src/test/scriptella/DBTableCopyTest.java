/*
 * Copyright 2006 The Scriptella Project Team.
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
package scriptella;

import scriptella.execution.ScriptsExecutor;
import scriptella.execution.ScriptsExecutorException;
import scriptella.expressions.ParametersCallback;
import scriptella.sql.JDBCException;
import scriptella.sql.Query;
import scriptella.sql.QueryCallback;

import java.sql.Connection;


/**
 * TODO: Add documentation
 *
 * @author Fyodor Kupolov
 * @version 1.0
 */
public class DBTableCopyTest extends DBTestCase {
    /**
     * This test copies a data from Table ot Table2
     */
    public void test() throws ScriptsExecutorException {
        final Connection con = getConnection("test");
        final ScriptsExecutor se = newScriptsExecutor("DBTableCopyTest.xml");
        se.execute();

        Query s = new Query("select * from test2");
        final int n[] = new int[]{0};

        s.execute(con,
                new QueryCallback() {
                    public void processRow(final ParametersCallback row) {
                        n[0]++;
                        assertEquals(n[0], row.getParameter("ID"));
                        if (n[0]==3) { //3rd row column value2 has 'value'
                            assertEquals("value", row.getParameter("value2"));
                        } else {
                            assertNull(row.getParameter("value2"));
                        }
                    }
                });
        assertEquals(n[0], 3);
    }

    /**
     * This test copies data from db1.Table to db2.Table2
     */
    public void test2() throws ScriptsExecutorException {
        final Connection con = getConnection("test");
        final Connection con2 = getConnection("test2");
        final ScriptsExecutor se = newScriptsExecutor("DBTableCopyTest2.xml");
        se.execute();

        Query s = new Query("select * from test2");
        final int n[] = new int[]{0};

        s.execute(con2,
                new QueryCallback() {
                    public void processRow(final ParametersCallback row) {
                        n[0]++;
                        assertEquals(n[0], row.getParameter("ID"));
                    }
                });
        assertEquals(n[0], 3);

        try {
            new Query("select * from test2").execute(con, null);
            fail("Test2 is absent in the 1st database");
        } catch (JDBCException e) {
            //OK
        }
    }
}
