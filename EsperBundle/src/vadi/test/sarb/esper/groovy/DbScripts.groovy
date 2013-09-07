package vadi.test.sarb.esper.groovy

import vadi.test.sarb.esper.db.*;

def initDB() {

def db = new DbUtil();
def sql = "create table position("
sql += " id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
sql += " symbol varchar(100), "
sql += " qty int,"
sql += " lors varchar(20), "
sql += " price float, "
sql += " cost float,"
sql += " date datetime)"

println "sql "+sql;
db.execute("drop table position;");
db.execute(sql);
println  db.execute("select * from position");

sql = "";
sql = "create table position_archive( "
sql += " id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
sql += " symbol varchar(100), "
sql += " qty int,"
sql += " lors varchar(20), "
sql += " price float, "
sql += " cost float,"
sql += " date datetime, "
sql += " curdate datetime default (CURRENT_TIMESTAMP()))"
println "sql "+sql
db.execute("drop table position_archive;");
db.execute(sql);
//sql="ALTER TABLE position_archive ADD CONSTRAINT contraint_name DEFAULT GETDATE() FOR curdate;"
//db.execute(sql);
println  db.execute("select * from position_archive");

sql="";
sql = "create table liquid_cash( "
sql += " cash float,"
sql+= " shortcash float ,"
sql += "currdate datetime default (current_timestamp()))";
print "sql "+sql
db.execute("drop table liquid_cash")
db.execute(sql)

}

def execute(sql) {
	def db = new DbUtil();
	for ( e in db.execute(sql))
	println e
	
}

def cleanDB(){
	def sql="delete  from position;";
	execute(sql)
	sql="delete  from position_archive";
	execute(sql)
	sql="delete from liquid_cash"
	execute(sql);
	sql="insert into liquid_cash (cash,shortcash) values (0,0)"
	execute(sql)
	
}
//initDB()

//sql = "insert into position (symbol,qty,lors,price,cost,date) values "
//sql += " ('sso',10,'buy',10,100,'2010-11-01')";
//cleanDB()
sql="select * from position"
execute(sql)
sql = "select * from position_archive  order by curdate asc"
execute(sql)
sql = "select * from  liquid_cash"
execute(sql)

//sql="select sum(price*qty)/sum(qty) as cb,lors,symbol from position_archive "+
//" group by symbol,lors"
//execute(sql)


