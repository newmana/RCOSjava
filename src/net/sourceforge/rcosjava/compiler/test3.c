/**
 * Tests integer and character outputs.
 **/
int main()
{
  int local;
  int local2;
  int local3;
  char hello[100] = "hello world";
  char a;

  local3 = (2 + 5);
  printf("local3 is:");
  printf("%i", local3);
  printf("\n");
  printf("print out 6:");
  printf("%i", 6);
  printf("\n");

  local = 3;
  local2 = 6;
  local = local2;
  local2 = (local2 + local);

  printf("local2 is: ");
  printf("%i", local2);
  printf("\n");

  local = (2 - local2);

  printf("local is: ");
  printf("%i", local);
  printf("\n");

  a = 'a';

  printf("%s", hello);
  printf("\n");
  printf("char a is: ");
  printf("%c", a);
  printf("\n");  

  hello[0] = 'j';
  hello[11] = 's';
  
  printf("Wobbly planets: ");
  printf("%s", hello);
  printf("\n");


  printf("First char in jello worlds: ");
  printf("%c", hello[0]);
  printf("\n");
  printf("Last char in jello worlds: ");
  printf("%c", hello[11]);
  printf("\n");
}
