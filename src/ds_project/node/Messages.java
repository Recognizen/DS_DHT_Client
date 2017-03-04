/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ds_project.node;

import java.io.Serializable;

/**
 *
 * @author recognition
 */
public class Messages {

    public static class Update implements Serializable {

        public final int keyId;
        public final String value;
        public final int version;

        public Update(int keyId, String value) {
            this.keyId = keyId;
            this.value = value;
            this.version = 0;
        }

        public Update(int keyId, String value, int version) {
            this.keyId = keyId;
            this.value = value;
            this.version = version;
        }
    }

    public static class GetKey implements Serializable {

        public final int keyId;

        public GetKey(int keyId) {
            this.keyId = keyId;
        }
    }

    public static class DataItem implements Serializable {

        public final ImmutableItem item;

        public DataItem(ImmutableItem item) {
            this.item = item;
        }
    }

    public static final class ImmutableItem implements Serializable {

        private final int key;
        private final String value;
        private final int version;

        public ImmutableItem(int key, String value, int version) {
            this.key = key;
            this.value = value;
            this.version = version;
        }

        public int getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public int getVersion() {
            return version;
        }
    }
    
    public static class Leave implements Serializable {
    } 
    
    public static class Terminated implements Serializable {
    } 
}
