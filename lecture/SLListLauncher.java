public class SLListLauncher {
    public static class IntList {
        public Integer first;
        public IntList next;

        public IntList(Integer i, IntList n) {
            first = i;
            next = n;
        }

        public void display() {
            String list = "( ";
            IntList p = this;
            while (p != null) {
                list = list + p.first + " ";
                p = p.next;
            }
            list = list + ")";
            System.out.println(list);
        }
    }

    public static IntList[] partition2(IntList lst, int k) {
        IntList[] array = new IntList[k];
        int index = 0;
        IntList L = lst;  // Assuming reverse() is a method that reverses a list.
        while (L != null) {
            //System.out.println("index");
            IntList prevAtIndex = array[index];//null//null//(1)
            //prevAtIndex.display();
            IntList next = L.next;//(2345)//(345)
            //next.display();
            array[index] = L;//(12345)//(2345)
            //array[index].display();
            array[index].next = prevAtIndex;//(1)//(2)
            //array[index].display();
            L = next;//(2345)//(345)
            //L.display();
            //System.out.println(index);
            //System.out.println(array.length);
            index = (index + 1) % array.length;//[1][0]
        }
        return array;
    }

    public static IntList[] partition(IntList lst, int k) {
        IntList[] array = new IntList[k];
        int index = 0;
        IntList L = lst;
        while (L != null) {
            IntList temp = array[index];
            array[index] = L;
            L = L.next;
            array[index].next = temp;
            index = (index + 1) % array.length;
                    }
        return array;
    }

    public static void main(String[] args) {
        IntList s = new IntList(1, null);
        s = new IntList(2, s);
        s = new IntList(3, s);
        s = new IntList(4, s);
        s = new IntList(5, s);
        s.display();
        IntList[] test = partition(s, 2);
        IntList t = new IntList(5, null);
        t = new IntList(4, t);
        t = new IntList(3, t);
        t = new IntList(2, t);
        t = new IntList(1, t);
        //t.display();
        //IntList[] test = partition2(t, 2);
        test[0].display();
        test[1].display();
        s.display();
        //t.display();
    }
}
