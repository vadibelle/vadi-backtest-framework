package vadi.test.sarb.esper.groovy

import groovy.json.JsonSlurper
import vadi.test.sarb.esper.db.*;
import vadi.test.sarb.esper.groovy.*
import vadi.test.sarb.esper.Messages
import groovy.sql.Sql
def initDB() {
	createTables()
}

def dropTable(name) {
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

def persistResult(map) {
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
	def output=''
	try {
		def db = new DbUtil().getConnection()
		def sc = new Sql(db)
		def ir =0
		def ivol=0
		def idd = 0
		def bhrow = [:]

		res = sc.rows("select * from results where symbol='SPY' and last_trade_indicator='BUYNHOLD'")
		//res = sc.rows("select * from results where symbol='SPY' ")
		println res
		assert res.size() != 0

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
		bhrow.each { println "BH return "+it }
		//	sc.eachRow("select symbol,returns,volatility,last_trade_indicator from results ") { row->println row }
		sc.eachRow("select symbol,returns,(returns-"+ir+")/"+ivol+" as sharpe ,volatility,drawdown,"
				+"volatility/"+ivol+" as relVol,drawdown/"+idd+" as relDD,"
				+"li,si,last_trade_indicator,last_trade_timestamp,last_trade_indicator,"
				+" long_position,short_position,long_short "
				+ " from results where last_trade_indicator !='BUYNHOLD'"
				+ " and last_trade_timestamp = (select max(last_trade_timestamp) from results ) "
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

			if ( tmp.getAt("SHARPE") > 0){
			//println tmp
			tmp.each { output += it.key+':'+it.value+','}
			output += '\n'
			}
		}
	}
	catch(e){
		println "Error calculating sharpe... please check the db"
		e.printStackTrace()
		return -1
	}
	output
}

ProcessArgs pArgs = new ProcessArgs(args)
println calcSharpe()
