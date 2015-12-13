#pragma once

#include <vector>
#include <string>
#include <map>

typedef unsigned int uint, behaelter;
typedef unsigned long long int bigInt;

struct Application
{

	uint _N;
	std::vector<behaelter> _behaelter;
	std::string _filename;

	std::map<std::tuple<std::vector<behaelter>, uint>, bigInt> memo;

	auto main(const std::vector<std::string>& arguments) -> int;

	auto loadFromFile(void) -> bool;
	auto getFromUser(void) -> void;

	auto anzahl(const std::vector<behaelter>& z, uint z_summe, uint n) -> bigInt;

};