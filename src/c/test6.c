/**
 * For reading in characters and integers.
 **/

int main()
{
  int count1, count2, total;

  for (count1 = 0; (count1 < 10); count1 = (count1 + 1))
  {
    for (count2 = 0; (count2 < 3); count2 = (count2 + 1))
    {
      printf("Count1: ");
      printf("%i", count1);
      printf("\n");
      printf("Count2: ");
      printf("%i", count2);
      printf("\n");
      total = (count1 + count2);
      printf("Total: ");
      printf("%i", total);
      printf("\n");
    }
  }
}
