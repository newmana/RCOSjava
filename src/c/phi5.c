/**
* philospher2.c
**/
int main()
{

   int semValue1;
   int semValue5;

   int semResult;
     semValue1 = semopen("sem1");
     semValue5 = semopen("sem5");

   while( ( 1== 1 ) )
   {


     semResult = semwait(semValue1);
     semResult = semwait(semValue5);
     printf("5 is eating.\n");
     semResult = semsignal(semValue1);
     semResult = semsignal(semValue5);
     printf("5 is thinking.\n");
     printf("");
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
