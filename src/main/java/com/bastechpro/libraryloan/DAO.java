package com.bastechpro.libraryloan;
/* library-loan
 * @created 01/10/2023
 * @author Konstantin Staykov
 */

public interface DAO {
    void setup() throws Exception;
    void connect() throws Exception;
    void close() throws Exception;

}
