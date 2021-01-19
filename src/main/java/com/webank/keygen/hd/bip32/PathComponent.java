package com.webank.keygen.hd.bip32;

/**
 * marker interface
 * @author aaronchu
 * @Description
 * @data 2020/11/18
 */
public class PathComponent {

    private boolean root;

    private PathComponent previous;
    private int index;
    private boolean hardended;

    public PathComponent(){
        root = true;
    }

    public PathComponent(PathComponent previous, int index){
        this(previous, index, false);
    }

    public PathComponent(PathComponent previous, int index, boolean hardended){
        if(index < 0) throw new IllegalArgumentException("only accept positive index");
        this.previous = previous;
        this.index = index;
        this.hardended = hardended;
    }

    public boolean isRoot(){
        return root;
    }

    public PathComponent getPrevious(){
        return previous;
    }

    public PathComponent next(int idx){
        return new PathComponent();
    }

    public int getIndex(){
        return index;
    }

    public boolean isHardened(){
        return hardended;
    }

    public String toString(){
        return currentPath();
    }

    public String currentPath(){
        if(isRoot()){
            return "m";
        }
        return previous.currentPath()+"/"+index+ (isHardened()?"\'":"");
    }
}
