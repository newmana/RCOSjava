/**
* philospher2.c
**/
int main()
{

   int semValue3;
   int semValue4;

   int semResult;
     semValue3 = semopen("sem3");
     semValue4 = semopen("sem4");

   while( ( 1== 1 ) )
   {


     semResult = semwait(semValue3);
     semResult = semwait(semValue4);
     printf("3 is eating.\n");
     semResult = semsignal(semValue3);
     semResult = semsignal(semValue4);
     printf("3 is thinking.\n");

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
