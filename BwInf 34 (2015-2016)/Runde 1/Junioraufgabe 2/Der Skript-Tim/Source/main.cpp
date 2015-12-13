#include <string>
#include <vector>
#include <iostream>

#include "./../inc/Application.hpp"

auto main(int argc, char** argv) -> int
{
	std::locale::global(std::locale("German_germany"));

	std::vector<std::string> arguments;

	for (int i = 1; i < argc; ++i)
		arguments.push_back(argv[i]);

	Application a;
	int res = a.main(arguments);

	std::cout << std::endl << std::endl;

	return res;
}
