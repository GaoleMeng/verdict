/*
 * Copyright 2017 University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.umich.verdict;

import java.sql.ResultSet;

import org.apache.spark.SparkContext;
//import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import edu.umich.verdict.dbms.DbmsSpark2;
import edu.umich.verdict.exceptions.VerdictException;
import edu.umich.verdict.query.Query;
import edu.umich.verdict.util.VerdictLogger;

/**
 * import edu.umich.verdict.VerdictSpark2Context<br/>
 * val vc = new VerdictSpark2Context(sc)<br/>
 * vc.sql("use mydatabase")<br/>
 * 
 * @author Yongjoo Park
 *
 */
public class VerdictSpark2Context extends VerdictContext {

    private Dataset<Row> df;
    private SparkContext this_sc;
    private  SparkSession this_session;


    public VerdictSpark2Context(SparkContext sc) throws VerdictException {
        this(sc, new VerdictConf());
    }

    public VerdictSpark2Context(SparkContext sc, VerdictConf conf) throws VerdictException {
        super(conf);
        conf.setDbms("spark2");
        this_sc =sc;
        SparkSession sparkSession = SparkSession.builder().getOrCreate();
        this_session = sparkSession;
        setDbms(new DbmsSpark2(this, sparkSession, conf));
        setMeta(new VerdictMeta(this));
    }

    @Override
    public void execute(String sql) throws VerdictException {
        VerdictLogger.debug(this, "An input query:");
        VerdictLogger.debugPretty(this, sql, "  ");
        Query vq = Query.getInstance(this, sql);
        df = vq.computeDataset();
    }

    @Override
    public ResultSet getResultSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Dataset<Row> getDataset() {
        return df;
    }

//    @Override
//    public DataFrame getDataFrame() {
//        return null;
//    }
    
    public Dataset<Row> sql(String sql) throws VerdictException {
        return executeSpark2Query(sql);
    }

    public SparkContext getThis_sc(){
        return this_sc;
    }

    public SparkSession getThis_session(){
        return this_session;
    }
}
