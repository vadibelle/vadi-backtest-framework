package vadi.test.sarb.esper.groovy

import groovy.json.JsonSlurper
import vadi.test.sarb.esper.db.*;
import vadi.test.sarb.esper.groovy.*
import vadi.test.sarb.esper.Messages
import groovy.sql.Sql
def initDB()
{
	createTables()
}
/*
def initDB_old() {

def db = new DbUtil();
//dropTable('position')

def sql = "create table position("
sql += " id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
sql += " symbol varchar(100), "
sql += " qty int,"
sql += " lors varchar(20), "
sql += " price float, "
sql += " cost float,"
sql += " date datetime)"

println "sql "+sql;

db.execute('drop table position')
db.execute(sql);

println  db.execute("select * from position");

//dropTable('position_archive')
sql = "";
sql = "create table position_archive( "
sql += " id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
sql += " symbol varchar(100), "
sql += " qty int,"
sql += " lors varchar(20), "
sql += " price float, "
sql += " cost float,"
sql += " date datetime, "
sql += " currdate datetime default (CURRENT_TIMESTAMP()))"
println "sql "+sql

db.execute("drop table position_archive");
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

sql = "";
sql = "create table signals( "
sql += " id  INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
sql += " signal varchar(255),"
sql += " currdate datetime default (current_timestamp()))"
db.execute('drop table signals')
db.execute(sql)

sql=""
sql = '''
CREATE TABLE RESULTS
(
   symbol varchar,
   cash float,
   total float,
   drawdown float,
   returns float,
   long_position int,
   no_of_trades int,
   no_of_stoploss int,
   last_price float,
   open_swing float,
   average_swing float,
average_volume float,
   volatility float,
   macd float,
   rsi float,
   short_position int,
   last_trade varchar,
   last_trade_close float,
   last_trade_type varchar,
   last_trade_indicator varchar,
   last_trade_timestamp timestamp,
   last_position varchar,
   last_postion_close float,
   last_position_type varchar,
   last_position_indicator varchar,
   last_poition_timestamp timestamp,
   li int ,
   rsint int,
   mli int,
   numSym int,
   lt int,
   st int,
   vlimit int,
   ml int,
   msi int,
   si int,
   currdate timestamp default  (current_timestamp())
)
;'''
db.execute('drop table results')
db.execute(sql)

}
*/
def dropTable(name)
{
	//def dt = "IF EXISTS ( SELECT [name] FROM sys.tables WHERE [name] = '"+name+"' )"+
	//' BEGIN '+
	def dt = 'DROP TABLE '+name
	//' go '
	//' END '
	println dt
	execute(dt)
}
def execute(sql) {
	def db = new DbUtil();
	for ( e in db.execute(sql))
	println e
	
}

def persistResult(map)
{
	if (map.getAt('last_trade') == null)
	return;
	println map
	 def ltr = [:]

	def close=''
	def type=''
	def ind =''
	def pr_ts=''
	def ls = 0
	if ( Messages.getString("long.short").equals("true"))
	ls = 1
	
	def jstring = map.getAt('last_trade').
	replace('TradeSignal','').replace('StopLoss','').
			replaceAll(/\[/,'').replaceAll(/\]/,'')
	println jstring
	jstring.split(',').each {
		def k = it.split('=')[0].stripIndent().stripMargin()
		def v = it.split('=')[1].stripIndent().stripMargin()
		ltr.put(k,v)
		if ( k.contains('close'))
			close = v
		if ( k.contains('indicator'))
			ind = v
		if ( k.contains('price_timestamp'))
			pr_ts = v
		if ( k.contains('type'))
			type = v
		}
	
	assert ltr.getAt('price_timestamp')	!= null
	def lpos = [:]
	def lastpos = map.getAt('last_position')
	if ( lastpos != null)
		lastpos = lastpos.replace('TradeSignal','').
			replaceAll(/\[/,'').replaceAll(/\]/,'')
			lastpos.split(',').each {
				def k = it.split('=')[0].stripIndent().stripIndent()
				def v = it.split('=')[1].stripIndent().stripMargin()
				lpos.put(k, v)
				/*if ( k.contains('close'))
				lpos.put('close', v)
				if ( k.contains('price_timestamp'))
				lpos.put('price_timestamp', v)
				if ( k.contains('type'))
				lpos.put('type', v)
				if ( k.contains('indicator'))
				lpos.put('indicator', v)*/
				
			}
	
			//println "vol "+map.getAt('volatility')
	def sql="""\
	insert into results (symbol,cash,total,drawdown,returns,volatility,macd,rsi,last_price,
	long_position,short_position,average_volume,average_swing,open_swing,no_of_trades,
	no_of_stoploss,last_trade,last_trade_close,last_trade_type,last_trade_indicator,
	last_trade_timestamp,last_position, last_postion_close ,last_position_type ,
   last_position_indicator , last_position_timestamp , li , rsint , mli ,
	numSym , lt , st , vlimit , ml ,  msi ,  si,long_short) values (
	"""
	sql += "'"+map.getAt('symbol')+"',"
	sql += map.getAt('cash')+','
	sql += map.getAt('total')+','
	sql += map.getAt('drawdown')+','
	sql += map.getAt('returns')+','
	sql += map.getAt('volatility')+','
	sql += map.getAt('macd')+','
	sql += map.getAt('rsi')+','
	sql += map.getAt('last_price')+','
	sql += map.getAt('long_position')+','
	sql += map.getAt('short_position')+','
	sql += map.getAt('average_volume')+','
	sql += map.getAt('average_swing')+','
	sql += map.getAt('open_swing')+','
	sql += map.getAt('no_of_trades')+','
	sql += map.getAt('no_of_stoploss')+','
	sql += "'"+map.getAt('last_trade')+"',"
	sql += close +','
	sql += "'"+type +"',"
	sql += "'"+ind+"',"
	sql += "'"+pr_ts+"',"
	sql += "'"+map.getAt('last_position')+"',"
	if (lpos.size() == 0)
	sql += null+','+null+','+null+','+null+','
	else
	sql+= lpos.getAt('close')+','+"'"+lpos.getAt('type')+"','"+lpos.getAt('indicator')+"','"+lpos.getAt('price_timestamp')+"',"
	
	sql += map.getAt('li')+','
	sql += map.getAt('rsint')+','
	sql += map.getAt('mli')+','
	sql += map.getAt('numSym')+','
	sql += map.getAt('lt')+','
	sql += map.getAt('st')+','
	sql += map.getAt('vlimit')+','
	sql += map.getAt('ml')+','
	sql += map.getAt('msi')+','
	sql += map.getAt('si')+','
	sql += ls+
	')'
	
	//println "SQL "+sql
	execute(sql)
		
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
	sql="delete from signals"
	execute(sql)
	sql="delete from results"
	execute(sql)
	
}

def createTables(){
	def sql = new File(Messages.getString('create.sql')).text
	//println sql
	println "Creating tables "+execute(sql)
}


//initDB()

//sql = "insert into position (symbol,qty,lors,price,cost,date) values "
//sql += " ('sso',10,'buy',10,100,'2010-11-01')";
//cleanDB()

/*
sql="select * from position"
execute(sql)
sql = "select * from position_archive  order by curdate asc"
execute(sql)
sql = "select * from  liquid_cash"
execute(sql)
sql= "select * from signals order by currdate asc"
execute (sql)
//sql="select sum(price*qty)/sum(qty) as cb,lors,symbol from position_archive "+
//" group by symbol,lors"
//execute(sql)

execute('select symbol,last_trade_timestamp from results order by last_trade_timestamp')
*/
//createTables()

def calcSharpe()
{
	def db = new DbUtil().getConnection()
	def sc = new Sql(db)
	def ir =0
	def ivol=0
	def idd = 0
	def bhrow = [:]
	sc.eachRow("select avg(returns) as avgret,avg(volatility) as avgvol ,avg(drawdown) avgdd from results where last_trade_indicator='BUYNHOLD'"
		+" and symbol='SPY'")
		 {row ->  
		ivol  += row.getAt('AVGVOL')
		ir += row.getAt("AVGRET")
		idd += row.getAt('avgdd')
		println row 
		}
	
	sc.eachRow("select symbol,avg(returns) as sret ,avg(volatility) svol,avg(drawdown) sdd from results "+
		"where last_trade_indicator='BUYNHOLD' group by symbol") { row ->
		//println row
		bhrow.put(row['SYMBOL'],row.toRowResult())
	} 
		bhrow.each {
			println "it "+it
		}
//	sc.eachRow("select symbol,returns,volatility,last_trade_indicator from results ") { row->println row }
	sc.eachRow("select symbol,returns,(returns-"+ir+")/"+ivol+" as sharpe ,volatility,drawdown,"
		+"volatility/"+ivol+" as relVol,drawdown/"+idd+" as relDD,"
		+"li,si,last_trade_indicator,last_trade_timestamp,"
		+" long_position,short_position,long_short "
		+ " from results where last_trade_indicator !='BUYNHOLD'"
		+" order by last_trade_timestamp,sharpe")
		{row->
		
			tmp = row.toRowResult()
			s = row['SYMBOL']
			br = bhrow[s]['sret']
			bd = bhrow[s]['sdd']
			println "s "+s+" d"+bd+" r"+br
			cr = row['returns']
			cd = row['drawdown']
			//println "s "+s+" d"+d+" cr "+cr+"cv "+cv+" cd "+cd
			
			tmp.put('relbhret',cr/br)
		
			tmp.put('relbhdd',cd/bd)
	
			println tmp
	}
		
	
}

ProcessArgs pArgs = new ProcessArgs(args)
calcSharpe()