package vadi.test.sarb.event

class ResetVariables  extends Event{
	def multiplier = 1 as float
	
	ResetVariables(m)
	{
		multiplier = m
	}

}
