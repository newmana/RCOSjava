/**
 * Tests if/else/elseif for >, ==, !=, <
 **/
void main()
{
  int local;
  int local2;

  printf("Global variable test\n");

  local = 3;
  local2 = 6;
  local = local2;
  local2 = (local2 + local);

  printf("local2 is: ");
  printf(%i, local2);

  local2 = (2 - local2);

  printf("local2 is: ");
  printf(%i, local2);
}
