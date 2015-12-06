#pragma once
#include <string>

using namespace std;

namespace Util
{
	static string wstrtostr(const wstring& in)
	{
		string res;
		res.resize(in.length());
		for (int i = 0; i < in.length(); i++)
			res[i] = in[i];
		return res;
	}

	static wstring strtowstr(const string& in)
	{
		wstring res;
		res.resize(in.length());
		for (int i = 0; i < in.length(); i++)
			res[i] = in[i];
		return res;
	}
}
