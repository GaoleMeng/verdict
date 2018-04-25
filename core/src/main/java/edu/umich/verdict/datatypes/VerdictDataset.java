package edu.umich.verdict.datatypes;

import org.apache.spark.SparkContext;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;




import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;



import java.sql.*;
import java.util.ArrayList;

import java.util.HashMap;

import java.util.Map;
import java.util.List;



public class VerdictDataset {


    private QueryResult qr;


    public VerdictDataset(Dataset<Row> ds){


        qr = new QueryResult();
        scala.Tuple2<String,String>[] column_names_type = ds.dtypes();
        List<Row> dsRows = ds.collectAsList();

        ArrayList<ColumnMetaData> cmdList = new ArrayList<>();
        int column_num = column_names_type.length;
        for(int i = 0 ; i < column_num ; i++){

            ColumnMetaData cmd = new ColumnMetaData();
            cmd.setColumnName(column_names_type[i]._1);
            cmd.setColumnType(column_names_type[i]._2);
            cmdList.add(cmd);
        }

        ArrayList<HashMap<String, Object>> rows = new ArrayList<>();
        for(int i = 0; i < dsRows.size() ; i++){
            HashMap tmp = new HashMap<String, Object>();
            for(int j = 0 ; j < column_num ; j++){
                String columnName = column_names_type[j]._1;
                Object content = dsRows.get(i).get(j);
                tmp.put(columnName,content);
            }
            rows.add(tmp);
        }

        qr.setColumnMetaData(cmdList);
        qr.setRows(rows);




    }
    public void deleteColumn(String columnLabel) throws SQLException {

        try {
            ArrayList<HashMap<String, Object>> rows = qr.getRows();
            if (rows.size() == 0) {
                throw new SQLException();
            }
            int columnNums = qr.getColumnMetaData().size();
            for (int i = 0; i < rows.size(); i++) {
                if (!rows.get(i).containsKey(columnLabel)) {
                    throw new SQLException();
                }
                rows.get(i).remove(columnLabel);
            }
            ArrayList<ColumnMetaData> metaData = qr.getColumnMetaData();
            for (int i = 0; i < columnNums; i++) {
                if (metaData.get(i).getColumnName().equals(columnLabel)) {
                    metaData.remove(i);
                    break;
                }
            }
            qr.setRows(rows);
            qr.setColumnMetaData(metaData);
        }
        catch (Exception e) {
            throw new SQLException();
        }
    }

    public boolean checkAndRevise(int threshold, float trust_bound) throws SQLException {

        boolean res = false;

        ArrayList<ColumnMetaData> metaData = qr.getColumnMetaData();
        int columnNums = metaData.size();
        boolean check_exists = false;
        for (int i = 0; i < columnNums; i++) {
            if (metaData.get(i).getColumnName().equals("_verdict_group_count")) {
                check_exists = true;
                break;
            }
        }

        if (!check_exists) {
            throw new SQLException();
        }

        // group count column exists, check every row of the verdictResultSet
        ArrayList<HashMap<String, Object>> rows = qr.getRows();
        for (int i = 1; i < columnNums; i++) {
            if (metaData.get(i).getColumnName().endsWith("_err") &&
                    metaData.get(i - 1).getColumnName().concat("_err").equals(metaData.get(i).getColumnName())) {
                for (int j = 0; j < rows.size(); j++) {
//                    Long tmp = (Long) rows.get(j).get("_verdict_group_count");

                    if (threshold >= (Long) rows.get(j).get("_verdict_group_count")) {
                        rows.get(j).put(metaData.get(i).getColumnName(), new Long(-1));
                        res = true;
                    }
                    else if (Float.parseFloat(rows.get(j).get(metaData.get(i).getColumnName()).toString()) >
                            trust_bound *
                                    Float.parseFloat(rows.get(j).get(metaData.get(i - 1).getColumnName()).toString())) {
                        rows.get(j).put(metaData.get(i).getColumnName(), new Long(-1));
                        res = true;
                    }
                }
            }
        }
        qr.setRows(rows);
        qr.setColumnMetaData(metaData);

        return res;
    }


    public Dataset<Row> convertToDS(SparkContext sc, SparkSession spark){

        List<Row>all_rows = new ArrayList<>();
        ArrayList<HashMap<String, Object>> rows =qr.getRows();
        for(int i = 0 ; i < rows.size() ; i++) {
            HashMap<String, Object> map = rows.get(i);
            Map<String, Object> map2 = new HashMap<String, Object>(map);

            String s ="";
            for (Map.Entry<String, Object> entry : map2.entrySet()) {

                s = s + entry.getValue().toString()+",";
            }
            s=s.substring(0,s.length()-1);
            String[] splits = s.split(",");
            Row row = RowFactory.create(splits);
            all_rows.add(row);
        }

        List<StructField> structfields = new ArrayList<StructField>();
        ArrayList<ColumnMetaData> metadata = qr.getColumnMetaData();

        for(ColumnMetaData c : metadata){
            structfields.add(DataTypes.createStructField( c.getColumnName(), DataTypes.StringType, false ));
        }

        StructType schema = DataTypes.createStructType(structfields);
        Dataset<Row> new_ds = spark.createDataFrame(all_rows,schema);

        return new_ds;
    }


}
