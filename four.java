import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

public class four{
    public static int now=0,flag=1,num=1,brackets=0;
    public static String[] step,i,C,S,c,k,p,inqt;
    public static Stack<String> st = new Stack<String>();
    public static Stack<String> symbol = new Stack<String>();
    public static List<String[]> qt = new ArrayList<String[]>();
    public static String f;
    public static void main(String[] args) throws Exception {
        String path_in = "./in.txt";
        String path_out = "./out.txt";
        //先进行语法分析,把结果存到out文件里：
        new analyzer().answer(path_in, path_out);
        //打开out文件,读取step和参照表：
        try{
            File filename = new File(path_out);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader);// 建立一个对象,它把文件内容转成计算机能读懂的语言
            String line = "";
            line= br.readLine().substring(1);//out.txt的第一行是{c,0}……等,substring(1)是因为analyzer搞得第一个是空格,切的时候会多一个
            step=line.split(" ");//切成数组
            line=br.readLine();//读第二行的i变量名参照表,下面同理
            i=line.substring(4,line.length()-1).replace(" ", "").split(",");//把参照表弄成数组
            line=br.readLine();
            C=line.substring(4,line.length()-1).replace(" ", "").split(",");
            line=br.readLine();
            S=line.substring(4,line.length()-1).replace(" ", "").split(",");
            line=br.readLine();
            c=line.substring(4,line.length()-1).replace(" ", "").split(",");
            line=br.readLine();
            k=line.substring(4,line.length()-1).replace(" ", "").split(",");
            line=br.readLine();
            p=line.substring(4,line.length()-1).replace(" ", "").split(",");
            br.close();
        }catch (Exception e) {
			e.printStackTrace();
        }
        //进行递归下降的生成四元组
        System.out.println(new four().answer());;
    }
    //文法：
    //   E -> T D
    //   D-> w T D|ε
    //   T -> F Y
    //   Y-> W F Y|ε
    //   F -> I | ( E ) 

    //四元组的生成,逻辑是：
    //在进入一个函数的时候把它入栈,然后离开这个函数的时候出栈
    //然后在E出栈的时候要生成一个四元组
    //E入栈的时候也往qt加个四元组,然后往下进行,F依次加进1,2,W和w加到0

    //以a*(b/c)为例
    //入栈E,qt.add(new String[4]),目前qt是{{ , , , }}
    //入栈T,
    //入栈F,当前字符为a,inqt=qt.get(qt.size()-1);inqt[1]==null所以inqt[1]="a";目前qt是{{ ,"a", , }},出栈F
    //入栈Y,当前字符为*,inqt[0]="*",目前qt是{{*,"a", , }}
    //入栈F,当前字符为（
    //入栈E,qt.add(new String[4]),目前qt是{{*,"a", , },{ , , , }}
    //入栈T,
    //入栈F,当前字符为b,inqt=qt.get(qt.size()-1);inqt[1]==null所以inqt[1]="b";
    //     目前qt是{{*,"a", , },{ ,"b", , }},出栈F
    //入栈Y,当前字符为/,inqt[0]="/",目前qt是{{*,"a", , },{/,"b", , }}
    //入栈F,当前字符为c,inqt=qt.get(qt.size()-1);inqt[1]!=null所以inqt[2]="c";目前qt是{{*,"a", , },{/,"b","c", }}
    //出栈出栈出栈出栈……
    //出栈E,inqt=qt.get(qt.size()-num);inqt[3]="t".concat(String.valueOf(num));num++;
    //     目前qt是{{*,"a", , },{/,"b","c",t1}}
    //出栈出栈出栈出栈……
    //出栈E,inqt=qt.get(qt.size()-num);if(inqt[2]==null) inqt[2]=qt.get(qt.size()-num+1)[3];
    //     inqt[3]="t".concat(String.valueOf(num));num++;
    //     目前qt是{{*,"a",t1,t2},{/,"b","c",t1}}

    //总结,
    //入栈E的操作：qt.add(new String[4])
    //出栈E的操作：inqt=qt.get(qt.size()-num);if(inqt[2]==null) inqt[2]=qt.get(qt.size()-num+1)[3];
    //            inqt[3]="t".concat(String.valueOf(num));num++;
    //入栈Y和D的操作,当前字符为wW时,inqt[0]=+-*/
    //入栈F,当前字符为i时,inqt=qt.get(qt.size()-1);if(inqt[1]==null) inqt[1]=i;else inqt[2]=i;

    //错误！！！
    //增加了+d后发现，不是在E时加四元组，而是在wW前一个的时候加
    //所以应该在F时检查下一个是否为+-*/，是则执行qt.add(new String[4])
    //有点算不明白何时执行"E出栈"的内容，重新推一下吧
    //重新推的时候发现都没有符号优先级的确定，完全是错误的。而且并不是在F时检查下一个是+-*/就可以执行qt.add(new String[4])的。
    //或许是入栈YD时操作的qt.add(new String[4])而预保留left的i

    //重新以a*(b-c)为例检测
    //入栈E，入栈T，
    //入栈F，当前字符"a",预保留String f=a;
    //出栈F，入栈T，
    //入栈Y，当前字符"*",所以*入符号栈。qt.add(new String[4]) inqt=qt.get(qt.size()-1);inqt[0]=t;inqt[1]=f;
    //入栈F，当前字符为（，（入符号栈。入栈E，入栈T，
    //入栈F，当前字符"b",f=b，出栈F
    //      （为什么这里的b不去填充上一个inqt[2]而是新建了一个四元组呢？）
    //      （建立符号栈来描述优先级，所有的p类型包括+-*/（）。遇到符号时入栈，填写对应inqt[2]时该符号出栈。）
    //入栈Y出栈Y，出栈T，
    //入栈D，当前符号为"-",栈顶为括号，所以入符号栈，qt.add(new String[4]) inqt=qt.get(qt.size()-1);inqt[0]=t;inqt[1]=f;
    //       那如果是a*b-c，-的优先级低，应该inqt[2]=f;f=null;inqt[3]="t".concat(String.valueOf(num));num++;
    //                    再symbol.pop();symbol.push("-");
    //                    再qt.add(new String[4]) inqt=qt.get(qt.size()-1);inqt[0]=t;inqt[1]=qt.get(qt.size()-num+1)[3];
    //入栈T，
    //入栈F，当前字符为"c",f=c,当前字符为）
    //出栈F，入栈Y出栈Y，出栈T，
    //出栈D,inqt=qt.get(qt.size()-num);if(inqt[2]==null) {
    //       if(f==null) inqt[2]=qt.get(qt.size()-num+1)[3];else{inqt[2]=f;f=null;}}
    //       if(inqt[3]==null) {inqt[3]="t".concat(String.valueOf(num));num++;symbol.pop();if(symbol.peek()=="(") symbol.pop();}
    //出栈Y，inqt=qt.get(qt.size()-num);if(inqt[2]==null) {
    //       if(f==null) inqt[2]=qt.get(qt.size()-num+1)[3];else{inqt[2]=f;f=null;}}
    //       if(inqt[3]==null) {inqt[3]="t".concat(String.valueOf(num));num++;symbol.pop();if(symbol.peek()=="(") symbol.pop();}

    //总结，
    //入栈Y入栈D，当前符号为wW那种，if(symbol==null) {symbol.push(t);qt.add(new String[4]) inqt=qt.get(qt.size()-1);inqt[0]=t;inqt[1]=f;}
    //           else if(symbol.peek().equals("(")||((symbol.peek().equals("+")||symbol.peek().equals("-"))&&(t.equals("*")||t.equals("/")))) {symbol.push(t);qt.add(new String[4]) inqt=qt.get(qt.size()-1);inqt[0]=t;inqt[1]=f;}
    //           else {inqt[2]=f;f=null;inqt[3]="t".concat(String.valueOf(num));num++;
    //                 symbol.pop();symbol.push("-");
    //                 qt.add(new String[4]) inqt=qt.get(qt.size()-1);inqt[0]=t;inqt[1]=qt.get(qt.size()-num+1)[3];}
    //出栈Y出栈D，inqt=qt.get(qt.size()-num);if(inqt[2]==null) {
    //       if(f==null) inqt[2]=qt.get(qt.size()-num+1)[3];else{inqt[2]=f;f=null;}}
    //       if(inqt[3]==null) {inqt[3]="t".concat(String.valueOf(num));num++;symbol.pop();if(symbol.peek()=="(") symbol.pop();}
    //入栈F，f=t;
    private void E(){
        if(flag==0) return;
        st.push("E");
        System.out.println(st.toString());
        System.out.println(symbol.toString());
        T();
        D();
        st.pop();
        System.out.println(st.toString());
    }
    private void T(){
        if(flag==0) return;
        st.push("T");
        System.out.println(st.toString());
        System.out.println(symbol.toString());
        F();
        Y();
        st.pop();
        System.out.println(st.toString());
    }
    private void D(){
        if(flag==0) return;
        st.push("D");
        System.out.println(symbol.toString());
        System.out.println(st.toString());
        String t;
        switch(step[now].substring(1,2)){
            case "i"://如果到D时step[now]是变量名,不造成错误,仍然继续
            t=i[Integer.parseInt(step[now].substring(3,4))];//查表找出对应变量名
            st.pop();
            return;
            case "c"://如果到D时step[now]是数字,不造成错误,仍然继续
            t=c[Integer.parseInt(step[now].substring(3,4))];//查表找出对应数字
            st.pop();
            return;
            case "p"://如果到D时step[now]是+-*/(),仍然继续,别的符号是错的
            t=p[Integer.parseInt(step[now].substring(3,4))];//查表找出对应符号
            if(t.equals("+")||t.equals("-")){
                System.out.println(t);
                if(now==step.length-1) {
                    flag=0;
                    return;
                };
                if(symbol.empty()) {symbol.push(t);qt.add(new String[4]);inqt=qt.get(qt.size()-1);inqt[0]=t;inqt[1]=f;for(String[] aa:qt){
                    for(String aaa:aa) System.out.println(aaa);
                }}
                else if(symbol.peek().equals("(")||((symbol.peek().equals("+")||symbol.peek().equals("-"))&&(t.equals("*")||t.equals("/")))) {
                    symbol.push(t);qt.add(new String[4]);inqt=qt.get(qt.size()-1);inqt[0]=t;inqt[1]=f;
                    for(String[] aa:qt){
                    for(String aaa:aa) System.out.println(aaa);
                }}
                else {inqt[2]=f;f=null;inqt[3]="t".concat(String.valueOf(num));num++;
                     symbol.pop();symbol.push("-");
                     qt.add(new String[4]);inqt=qt.get(qt.size()-1);inqt[0]=t;inqt[1]=qt.get(qt.size()-num+1)[3];for(String[] aa:qt){
                        for(String aaa:aa) System.out.println(aaa);
                    }}
                now++;//+和-只在Y里消除,别的地方遇到都是错的
                T();
                D();
                break;
            }
            else if(t.equals("*")||t.equals("/")||t.equals("(")||t.equals(")")) {st.pop();return;}
            else{
                flag=0;
                return;
            }
            default:
            flag=0;
            return;
        }    
        st.pop();
        System.out.println(st.toString());
        inqt=qt.get(qt.size()-num);if(inqt[2]==null) {
            if(f==null) inqt[2]=qt.get(qt.size()-num+1)[3];else{inqt[2]=f;f=null;}for(String[] aa:qt){
                for(String aaa:aa) System.out.println(aaa);
            }}
        if(inqt[3]==null) {inqt[3]="t".concat(String.valueOf(num));num++;symbol.pop();if(symbol.peek()=="(") symbol.pop();for(String[] aa:qt){
            for(String aaa:aa) System.out.println(aaa);
        }}
    }
    private void Y(){
        if(flag==0) return;
        st.push("Y");
        System.out.println(st.toString());
        System.out.println(symbol.toString());
        String t;
        switch(step[now].substring(1,2)){
            case "i"://如果到D时step[now]是变量名,则应该转换为变量名
            t=i[Integer.parseInt(step[now].substring(3,4))];//查表找出对应变量名
            System.out.println(t);
            if(now==step.length-1) {st.pop();System.out.println(st.toString());return;} 
            now++;
            st.pop();
            return;
            case "c"://如果到D时step[now]是数字,则应该转换为数字
            t=c[Integer.parseInt(step[now].substring(3,4))];//查表找出对应数字
            System.out.println(t);
            if(now==step.length-1) {st.pop();System.out.println(st.toString());return;} 
            now++;
            st.pop();
            return;
            case "p"://如果到D时step[now]是+-*/(),仍然继续,别的符号是错的
            t=p[Integer.parseInt(step[now].substring(3,4))];//查表找出对应符号
            if(t.equals("*")||t.equals("/")){
                System.out.println(t);
                if(now==step.length-1) {
                    flag=0;
                    return;
                };
                if(symbol.empty()) {symbol.push(t);qt.add(new String[4]);inqt=qt.get(qt.size()-1);inqt[0]=t;inqt[1]=f;for(String[] aa:qt){
                    for(String aaa:aa) System.out.println(aaa);
                }}
                else if(symbol.peek().equals("(")||((symbol.peek().equals("+")||symbol.peek().equals("-"))&&(t.equals("*")||t.equals("/")))) {symbol.push(t);qt.add(new String[4]);inqt=qt.get(qt.size()-1);inqt[0]=t;inqt[1]=f;for(String[] aa:qt){
                    for(String aaa:aa) System.out.println(aaa);
                }}
                else {inqt[2]=f;f=null;inqt[3]="t".concat(String.valueOf(num));num++;
                     symbol.pop();symbol.push("-");
                     qt.add(new String[4]);inqt=qt.get(qt.size()-1);inqt[0]=t;inqt[1]=qt.get(qt.size()-num+1)[3];for(String[] aa:qt){
                        for(String aaa:aa) System.out.println(aaa);
                    }}
                now++;//*和/只在Y里消除,别的地方遇到都是错的
                F();
                Y();
                break;
            }
            else if(t.equals("+")||t.equals("-")||t.equals("(")||t.equals(")")) {st.pop();return;}
            else{
                flag=0;
                return;
            }
            default:
            flag=0;
            return;
        }    
        st.pop();
        System.out.println(st.toString());
        inqt=qt.get(qt.size()-num);if(inqt[2]==null) {
        if(f==null) inqt[2]=qt.get(qt.size()-num+1)[3];else{inqt[2]=f;f=null;}for(String[] aa:qt){
            for(String aaa:aa) System.out.println(aaa);
        }}
        if(inqt[3]==null) {inqt[3]="t".concat(String.valueOf(num));num++;symbol.pop();if(!symbol.empty()){if(symbol.peek()=="(") symbol.pop();} 
        for(String[] aa:qt){
            for(String aaa:aa) System.out.println(aaa);
        }}
    }

    //入栈F,当前字符为i时,inqt=qt.get(qt.size()-1);if(inqt[1]==null) inqt[1]=i;else inqt[2]=i;
    private void F(){
        st.push("F");
        System.out.println(st.toString());
        System.out.println(symbol.toString());
        if(flag==0) return;
        String t;
        switch(step[now].substring(1,2)){
            case "i":
            t=i[Integer.parseInt(step[now].substring(3,4))];//查表找出对应变量名 
            System.out.println(t);
            f=t;
            if(now==step.length-1) {st.pop();System.out.println(st.toString());return;} 
            now++;
            if(step[now].substring(1,2).equals("p")){
                t=p[Integer.parseInt(step[now].substring(3,4))];
                if(t.equals(")")){
                    brackets--;
                    System.out.println(t);
                    if(now==step.length-1) {st.pop();System.out.println(st.toString());return;} 
                    now++;
                    if(brackets<0){
                        flag=0;
                        return;
                    }
                }
            }
            break;
            case "c":
            t=c[Integer.parseInt(step[now].substring(3,4))];//查表找出对应数字
            System.out.println(t);
            f=t;
            if(now==step.length-1) {st.pop();System.out.println(st.toString());return;} 
            now++;
            if(step[now].substring(1,2).equals("p")){
                t=p[Integer.parseInt(step[now].substring(3,4))];
                if(t.equals(")")){
                    brackets--;
                    System.out.println(t);
                    if(now==step.length-1) {st.pop();System.out.println(st.toString());return;} 
                    now++;
                    if(brackets<0){
                        flag=0;
                        return;
                    }
                }
            }
            break;
            case "p"://如果到D时step[now]是符号,只接受（,别的都是错的
            t=p[Integer.parseInt(step[now].substring(3,4))];//查表找出对应符号
            if(t.equals("(")){
                System.out.println(t);
                if(now==step.length-1) {
                    flag=0;
                    return;
                }
                else if(step[now+1].substring(1,2).equals("p")&&p[Integer.parseInt(step[now+1].substring(3,4))].equals(")")){
                    flag=0;
                    return;
                }
                now++;
                brackets++;
                symbol.push("(");
                E();
                break;
            }
            else{
                flag=0;
                return;
            }
            default:
            flag=0;
            return;
        }   
        st.pop();
        System.out.println(st.toString()); 
    }
    public String answer() {
        E();
        for(String[] aa:qt){
            for(String aaa:aa) System.out.println(aaa);
        }
        System.out.println("结果应为* a t1 t2  - b c t1");
        if(brackets!=0||flag==0||now!=step.length-1) return "wrong";
        else return "right";
    }
}