/**
* philospher2.c
**/
int main()
{

   int semValue4;
   int semValue5;

   int semResult;
     semValue4 = semopen("sem4");
     semValue5 = semopen("sem5");

   while( ( 1== 1 ) )
   {


     semResult = semwait(semValue4);
     semResult = semwait(semValue5);
     printf("4 is eating.\n");
     semResult = semsignal(semValue4);
     semResult = semsignal(semValue5);
     printf("4 is thinking.\n");
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
