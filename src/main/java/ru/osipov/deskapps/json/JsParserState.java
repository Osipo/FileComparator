package ru.osipov.deskapps.json;

public enum JsParserState {
    START,OPENROOT,CLOSEROOT,OPENBRACE,CLOSEBRACE,OPENARR,ARRELEM,CLOSEARR,OPENQ,CLOSEQ,OPENQP,CLOSEQP,COLON,ERR,FINISH;
}
