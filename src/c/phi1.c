/**
* philospher1.c
**/
int main()
{
   int semValue1;
   int semValue2;
   int semValue3;
   int semValue4;
   int semValue5;
   int semResult;
     semValue1 = semcreate("sem1",1);
     semValue2 = semcreate("sem2",1);
     semValue3 = semcreate("sem3",1);
     semValue4 = semcreate("sem4",1);
     semValue5 = semcreate("sem5",1);
   while( ( 1 == 1 ) )
   {

     /*printf("1 is waiting sem1.\n");*/
     semResult = semwait(semValue1);
     /*printf("1 has got sem1.\n");*/
     /*printf("1 is waiting sem2.\n");*/
     semResult = semwait(semValue2);
     /*printf("1 has got sem2.\n");*/

     printf("1 is eating.\n");
     semResult = semsignal(semValue1);
    /* printf("1 signal sem1.\n");*/
     semResult = semsignal(semValue2);
     /*printf("1 signal sem1.\n");*/

     printf("1 is thinking.\n");
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
