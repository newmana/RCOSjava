char str[100];

void main()
{
  int count;

  count = 2;
  str = "Hello World\n";

  if ((count > 0))
  {
    printf("Local count is: ");
    printf("%i", count);
    printf("\n");
    printf("Global str is: ");
    printf("%s", str);
  }
}

