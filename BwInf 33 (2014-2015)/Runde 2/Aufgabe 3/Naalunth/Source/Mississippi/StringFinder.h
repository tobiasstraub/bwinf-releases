#pragma once
#include <map>
#include <string>
#include <vector>
using namespace std;


struct PosLen
{
	int pos;
	int len;

	bool operator<(const PosLen& other) const{
		return (pos == other.pos) ? (len < other.len) : (pos < other.pos);
	}
	PosLen operator-(const PosLen& other) const{
		return PosLen{ pos - other.pos, len - other.len };
	}
};

//This class is just used to clearly define what needs to be done
class StringFinder
{
public:
	//Change the text associated with this StringFinder
	void SetString(string* inputSequence)
	{
		text_ = inputSequence;
		OnStringChange(inputSequence);
	}


	//Returns all Substrings matching the specifications
	virtual map<PosLen, vector<int> > GetAllSubStrings(int minLength = 1, int minAmount = 2) = 0;
	virtual ~StringFinder() {};

protected:
	virtual void OnStringChange(string* in) = 0;
	string* text_;
};
