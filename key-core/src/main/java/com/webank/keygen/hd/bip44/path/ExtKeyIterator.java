package com.webank.keygen.hd.bip44.path;

import com.webank.keygen.hd.bip32.ExtendedPrivateKey;
import com.webank.keygen.utils.ExtendedKeyUtil;

import java.util.Iterator;

/**
 * @author aaronchu
 * @Description
 * @data 2020/11/19
 */
final class ExtKeyIterator implements Iterator<ExtendedPrivateKey> {

    private final String pathString;


    private int charIndex;
    private int childIndex;
    private ExtendedPrivateKey current = null;

    public ExtKeyIterator(PathTokenLooper tpath){
        this.current = tpath.getRootKey();
        this.pathString = tpath.getPathString();
        this.charIndex= 1;//skip m
    }

    @Override
    public boolean hasNext() {
        return charIndex < pathString.length();
    }

    @Override
    public ExtendedPrivateKey next() {
        //Slash check
        char ch = pathString.charAt(charIndex++);
        if(ch != '/') throw new IllegalArgumentException("Illegal pathString "+pathString);
        //Get component
        while(charIndex < pathString.length()){
            ch = pathString.charAt(charIndex);
            boolean tokenEnd = process(ch);
            if(!tokenEnd){
                break;
            }
            charIndex++;
        }
        this.current = this.current.deriveChild(this.childIndex);
        this.childIndex = 0;
        return this.current;

        //derive item
    }

    private boolean process(char ch){
        //May be should use state machine pattern
        boolean continueProcess = true;
        switch (ch){
            case '\'':{//Hardened mark
                this.childIndex = ExtendedKeyUtil.hardIndex(childIndex);
                break;
            }
            case '/':{//Token ending
                continueProcess = false;
                break;
            }
            default:{
                //number
                if(ch < '0' || ch > '9') throw new IllegalArgumentException("invalid char "+ch);
                childIndex *= 10;
                int num = ch - '0';
                childIndex+=num;
            }
        }
        return continueProcess;
    }
}
