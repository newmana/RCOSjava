/**
* philospher2.c
**/
int main()
{

   int semValue2;
   int semValue3;

   int semResult;
     semValue2 = semopen("sem2");
     semValue3 = semopen("sem3");

   while( ( 1== 1 ) )
   {

     /* printf("2 is waiting sem2.\n");*/
     semResult = semwait(semValue2);
      /*printf("2 has got sem2.\n");*/
      /*printf("2 is waiting sem3.\n");*/

     semResult = semwait(semValue3);
    /* printf("2 has got sem3.\n");*/

     printf("2 is eating.\n");

     semResult = semsignal(semValue2);
     /* printf("2 signal sem2.\n");*/
     semResult = semsignal(semValue3);
    /* printf("2 signal sem3.\n");*/

     printf("2 is thinking.\n");
printf("");
printf("");
printf("");
printf("");
printf("");
printf("");
printf("");

   }

   /*semResult = semclose(semValue);*/
}
