package net.dozensbit.benchmark;

import java.sql.*;

/**
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class H2Test
{
    private final int OBJECTS_COUNT;
    private final int TEST_COUNT;

    private Statement stat;

    public H2Test(final int objectsCount, final int testsCount)
    {
        OBJECTS_COUNT = objectsCount;
        TEST_COUNT = testsCount;
    }

    public void allInclusiveSingleThread()
    {
        System.out.println("Test started!");
        try {

            Class.forName("org.h2.Driver");

            //create database on memory
            Connection con = DriverManager.getConnection("jdbc:h2:mem:mytest", "sa", "");

            stat = con.createStatement();

            //create table
            stat.execute("CREATE TABLE ACTIVITY (ID INTEGER, ATTR_1 VARCHAR(10), ATTR_2 VARCHAR(10), ATTR_3 VARCHAR(15)," +
                    "ATTR_4 VARCHAR(10), ATTR_5 VARCHAR(10), ATTR_6 VARCHAR(10), ATTR_7 VARCHAR(10), ATTR_8 VARCHAR(10)," +
                    "ATTR_9 VARCHAR(10), ATTR_10 VARCHAR(10), PRIMARY KEY (ID))");

            //prepared statement
            PreparedStatement prep = con.prepareStatement("INSERT INTO ACTIVITY " +
                    "(ID, ATTR_1, ATTR_2, ATTR_3, ATTR_4, ATTR_5, ATTR_6, ATTR_7, ATTR_8, ATTR_9, ATTR_10) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?)");


            int ROW_COUNT = 100000;
            int TESTS_COUNT = 1000;

            for (int i = 0; i < ROW_COUNT; i++){
                prep.setLong(1, i);
                prep.setString(2, "omsk");
                prep.setString(3, "tomsk");
                prep.setString(4, "novosibirsk");
                prep.setString(5, "moscow");
                prep.setString(6, "male");
                prep.setString(7, "ru");
                prep.setString(8, "de");
                prep.setString(9, "en");
                prep.setString(10, "au");
                prep.setString(11, "it");

                prep.addBatch();
            }
            con.setAutoCommit(false);
            prep.executeBatch();
            con.setAutoCommit(true);

          /*stat.execute("CREATE INDEX IDX_1 ON ACTIVITY(ATTR_1);");
            stat.execute("CREATE INDEX IDX_2 ON ACTIVITY(ATTR_2);");
            stat.execute("CREATE INDEX IDX_3 ON ACTIVITY(ATTR_3);");
            stat.execute("CREATE INDEX IDX_4 ON ACTIVITY(ATTR_4);");
            stat.execute("CREATE INDEX IDX_5 ON ACTIVITY(ATTR_5);");
            stat.execute("CREATE INDEX IDX_6 ON ACTIVITY(ATTR_6);");
            stat.execute("CREATE INDEX IDX_7 ON ACTIVITY(ATTR_7);");
            stat.execute("CREATE INDEX IDX_8 ON ACTIVITY(ATTR_8);");
            stat.execute("CREATE INDEX IDX_9 ON ACTIVITY(ATTR_9);");
            stat.execute("CREATE INDEX IDX_10 ON ACTIVITY(ATTR_10);");*/

            long sumTime = 0;
            for (int i = 0; i < TESTS_COUNT; i++) {
                sumTime+= execQuery();
            }

            System.out.println("Rows count: " + ROW_COUNT);
            System.out.println("Tests count: " + TESTS_COUNT);
            System.out.println(
                    "Average time: " + (sumTime / TESTS_COUNT) / 1000000.0 + " ms"
            );


            //close connection
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Test finished");
    }


    private long execQuery() throws SQLException
    {
        //query to database
        long start = System.nanoTime();

        ResultSet rs = stat.executeQuery(
                "SELECT * FROM ACTIVITY WHERE " +
                        "ATTR_1='omsk' AND " +
                        "ATTR_2='tomsk' AND " +
                        "ATTR_3='novosibirsk' AND " +
                        "ATTR_4='moscow' AND " +
                        "ATTR_5='male' AND " +
                        "ATTR_6='ru' AND " +
                        "ATTR_7='de' AND " +
                        "ATTR_8='en' AND " +
                        "ATTR_9='au' AND " +
                        "ATTR_10='it' "
        );


        while (rs.next()) {

            String a1 = rs.getString(2);
            String a2 = rs.getString(3);
            String a3 = rs.getString(4);
            String a4 = rs.getString(5);
            String a5 = rs.getString(6);
            String a6 = rs.getString(7);
            String a7 = rs.getString(8);
            String a8 = rs.getString(9);
            String a9 = rs.getString(10);
            String a10 = rs.getString(11);
        }

        rs.close();


        return System.nanoTime() - start;
    }
}
