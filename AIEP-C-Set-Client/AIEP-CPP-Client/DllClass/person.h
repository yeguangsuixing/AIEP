







class __declspec(dllexport) CPerson
{
private:
	char szName[128];
	int age;
public:
	CPerson();
	~CPerson();
	CPerson(char* lpName,int nAge);
	friend  void createObject(int x);
public:
	char *getName();
	int getAge();
};