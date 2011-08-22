var _;
function nullMethod(){
}

function Object_0(){
}

_ = Object_0.prototype = {};
_.getClass$ = function getClass_0(){
  return Ljava_lang_Object_2_classLit;
}
;
_.hashCode$ = function hashCode_0(){
  return this.$H || (this.$H = ++sNextHashId);
}
;
_.toString$ = function toString_0(){
  return this.getClass$().typeName + '@' + toPowerOfTwoString(this.hashCode$());
}
;
_.toString = function(){
  return this.toString$();
}
;
_.typeMarker$ = nullMethod;
_.castableTypeMap$ = {};
function $registerWorker(this$static){
  var handler = this$static;
  google.gears.workerPool.onmessage = function(a, b, message){
    fireOnMessage(handler, message);
  }
  ;
}

function fireOnMessage(handler, event_0){
  var msg;
  if (isA(event_0.body)) {
    msg = event_0.body;
    $moduleBase = msg.moduleBaseURL;
  }
   else {
    $onMessageReceived(event_0);
  }
}

function AbstractWorkerEntryPoint(){
}

_ = AbstractWorkerEntryPoint.prototype = new Object_0;
_.getClass$ = function getClass_1(){
  return Lcom_bedatadriven_rebar_worker_client_AbstractWorkerEntryPoint_2_classLit;
}
;
_.castableTypeMap$ = {};
function $onMessageReceived(event_0){
  var $e0_0, cmd, e, logger, rowsAffected;
  cmd = event_0.body;
  logger = new WorkerLogger_0(google.gears.workerPool, event_0.sender, cmd.executionId);
  try {
    rowsAffected = $execute(new GearsUpdateExecutor_0(cmd, logger));
    google.gears.workerPool.sendMessage({executionId:cmd.executionId, type:3, rowsAffected:rowsAffected}, event_0.sender);
  }
   catch ($e0) {
    $e0 = caught($e0);
    if (instanceOf($e0, 2)) {
      e = $e0;
      google.gears.workerPool.sendMessage({executionId:cmd.executionId, type:1, message:e.getMessage()}, event_0.sender);
    }
     else 
      throw $e0;
  }
}

function GearsSqlWorker_0(){
}

function GearsSqlWorker(){
}

_ = GearsSqlWorker_0.prototype = GearsSqlWorker.prototype = new AbstractWorkerEntryPoint;
_.getClass$ = function getClass_2(){
  return Lcom_bedatadriven_rebar_sql_worker_client_GearsSqlWorker_2_classLit;
}
;
_.castableTypeMap$ = {};
function $beginTransaction(this$static){
  var $e0_0, $e1_0, startTime, d, d_0;
  startTime = (d = new Date , d.getMilliseconds());
  $log(this$static.logger, 'Starting attempt to obtain lock...');
  while (true) {
    try {
      this$static.database = ($wnd.google && ($wnd.google.gears && $wnd.google.gears.factory)).create('beta.database');
      this$static.database.open(this$static.cmd.databaseName);
      $execute_0(this$static.database, 'BEGIN EXCLUSIVE TRANSACTION', initValues(_3Ljava_lang_String_2_classLit, {}, 1, []));
      return;
    }
     catch ($e0) {
      $e0 = caught($e0);
      if (instanceOf($e0, 2)) {
        if (this$static.database) {
          try {
            $close(this$static.database);
          }
           catch ($e1) {
            $e1 = caught($e1);
            if (!instanceOf($e1, 2))
              throw $e1;
          }
        }
        if ((d_0 = new Date , d_0.getMilliseconds()) > startTime + 60000) {
          throw new RuntimeException_0;
        }
        $log(this$static.logger, 'Database locked, retrying...');
      }
       else 
        throw $e0;
    }
  }
}

function $closeConnection(this$static){
  var $e0_0, e;
  try {
    $close(this$static.database);
  }
   catch ($e0) {
    $e0 = caught($e0);
    if (instanceOf($e0, 3)) {
      e = $e0;
      $log_0(this$static.logger, 'Exception thrown while closing the database: ', e);
    }
     else 
      throw $e0;
  }
}

function $execute(this$static){
  var $e0_0, e, rowsAffected;
  rowsAffected = 0;
  try {
    $beginTransaction(this$static);
    rowsAffected = $executeUpdates(this$static, this$static.cmd.operations);
    $execute_0(this$static.database, 'commit', initValues(_3Ljava_lang_String_2_classLit, {}, 1, []));
  }
   catch ($e0) {
    $e0 = caught($e0);
    if (instanceOf($e0, 2)) {
      e = $e0;
      $rollbackSavePoint(this$static);
      throw e;
    }
     else 
      throw $e0;
  }
   finally {
    $closeConnection(this$static);
  }
  return rowsAffected;
}

function $executeUpdates(this$static, ops){
  var i, j, rowsAffectedCount, stmt;
  rowsAffectedCount = 0;
  for (i = 0; i != ops.length; ++i) {
    stmt = ops[i];
    if (!stmt.executions || stmt.executions.length == 0) {
      $execute_0(this$static.database, stmt.statement, initValues(_3Ljava_lang_String_2_classLit, {}, 1, []));
    }
     else {
      for (j = 0; j != stmt.executions.length; ++j) {
        this$static.database.execute(stmt.statement, stmt.executions[j]);
        rowsAffectedCount += this$static.database.rowsAffected;
      }
    }
  }
  return rowsAffectedCount;
}

function $rollbackSavePoint(this$static){
  var $e0_0, e;
  try {
    $execute_0(this$static.database, 'rollback', initValues(_3Ljava_lang_String_2_classLit, {}, 1, []));
  }
   catch ($e0) {
    $e0 = caught($e0);
    if (instanceOf($e0, 2)) {
      e = $e0;
      $log_0(this$static.logger, e.getMessage(), e);
    }
     else 
      throw $e0;
  }
}

function GearsUpdateExecutor_0(cmd, logger){
  this.cmd = cmd;
  this.logger = logger;
}

function GearsUpdateExecutor(){
}

_ = GearsUpdateExecutor_0.prototype = GearsUpdateExecutor.prototype = new Object_0;
_.getClass$ = function getClass_3(){
  return Lcom_bedatadriven_rebar_sql_worker_client_GearsUpdateExecutor_2_classLit;
}
;
_.castableTypeMap$ = {};
_.cmd = null;
_.database = null;
_.logger = null;
function $log(this$static, message){
  !!this$static.pool && (this$static.pool.sendMessage({executionId:this$static.executionId_0, type:2, message:message}, this$static.ownerWorkerId) , undefined);
}

function $log_0(this$static, message, e){
  !!this$static.pool && (this$static.pool.sendMessage({executionId:this$static.executionId_0, type:2, message:message + e.getMessage()}, this$static.ownerWorkerId) , undefined);
}

function WorkerLogger_0(pool, ownerWorkerId, executionId){
  this.pool = pool;
  this.ownerWorkerId = ownerWorkerId;
  this.executionId_0 = executionId;
}

function WorkerLogger(){
}

_ = WorkerLogger_0.prototype = WorkerLogger.prototype = new Object_0;
_.getClass$ = function getClass_4(){
  return Lcom_bedatadriven_rebar_sql_worker_client_WorkerLogger_2_classLit;
}
;
_.castableTypeMap$ = {};
_.executionId_0 = 0;
_.ownerWorkerId = 0;
_.pool = null;
function isA(jso){
  return jso && jso._isInit;
}

function $setStackTrace(stackTrace){
  var c, copy, i;
  copy = initDim(_3Ljava_lang_StackTraceElement_2_classLit, {}, 0, stackTrace.length, 0);
  for (i = 0 , c = stackTrace.length; i < c; ++i) {
    if (!stackTrace[i]) {
      throw new NullPointerException_0;
    }
    copy[i] = stackTrace[i];
  }
}

function Throwable(){
}

_ = Throwable.prototype = new Object_0;
_.getClass$ = function getClass_5(){
  return Ljava_lang_Throwable_2_classLit;
}
;
_.getMessage = function getMessage(){
  return this.detailMessage;
}
;
_.toString$ = function toString_1(){
  var className, msg;
  className = this.getClass$().typeName;
  msg = this.getMessage();
  return msg != null?className + ': ' + msg:className;
}
;
_.castableTypeMap$ = {6:1};
_.detailMessage = null;
function Exception(){
}

_ = Exception.prototype = new Throwable;
_.getClass$ = function getClass_6(){
  return Ljava_lang_Exception_2_classLit;
}
;
_.castableTypeMap$ = {2:1, 6:1};
function RuntimeException_0(){
  $fillInStackTrace();
  this.detailMessage = 'Failed to obtain a lock after wating for 60000 ms';
}

function RuntimeException(){
}

_ = RuntimeException_0.prototype = RuntimeException.prototype = new Exception;
_.getClass$ = function getClass_7(){
  return Ljava_lang_RuntimeException_2_classLit;
}
;
_.castableTypeMap$ = {2:1, 4:1, 6:1};
function $init(this$static){
  this$static.name_0 = getName(this$static.e);
  this$static.description = getDescription(this$static.e);
  this$static.message_0 = '(' + this$static.name_0 + '): ' + this$static.description + getProperties(this$static.e);
}

function JavaScriptException_0(e){
  $fillInStackTrace();
  this.e = e;
  $createStackTrace(this);
}

function getDescription(e){
  return instanceOfJso(e)?e == null?null:e.message:e + '';
}

function getName(e){
  var maybeJsoInvocation;
  return e == null?'null':instanceOfJso(e)?e == null?null:e.name:e != null && (e.castableTypeMap$ && !!e.castableTypeMap$[1])?'String':(maybeJsoInvocation = e , maybeJsoInvocation.typeMarker$ == nullMethod || maybeJsoInvocation.castableTypeMap$ && !!maybeJsoInvocation.castableTypeMap$[1]?maybeJsoInvocation.getClass$():Lcom_google_gwt_core_client_JavaScriptObject_2_classLit).typeName;
}

function getProperties(e){
  return instanceOfJso(e)?$getProperties(e):'';
}

function JavaScriptException(){
}

_ = JavaScriptException_0.prototype = JavaScriptException.prototype = new RuntimeException;
_.getClass$ = function getClass_8(){
  return Lcom_google_gwt_core_client_JavaScriptException_2_classLit;
}
;
_.getDescription = function getDescription_0(){
  return this.message_0 == null && $init(this) , this.description;
}
;
_.getMessage = function getMessage_0(){
  this.message_0 == null && $init(this);
  return this.message_0;
}
;
_.castableTypeMap$ = {2:1, 4:1, 5:1, 6:1};
_.description = null;
_.e = null;
_.message_0 = null;
_.name_0 = null;
function Scheduler(){
}

_ = Scheduler.prototype = new Object_0;
_.getClass$ = function getClass_9(){
  return Lcom_google_gwt_core_client_Scheduler_2_classLit;
}
;
_.castableTypeMap$ = {};
function apply(jsFunction, thisObj, arguments_0){
  return jsFunction.apply(thisObj, arguments_0);
  var __0;
}

function enter(){
  if (entryDepth++ == 0) {
    $flushEntryCommands(($clinit() , INSTANCE));
    return true;
  }
  return false;
}

function entry(jsFunction){
  return function(){
    try {
      return entry0(jsFunction, this, arguments);
    }
     catch (e) {
      throw e;
    }
  }
  ;
}

function entry0(jsFunction, thisObj, arguments_0){
  var initialEntry;
  initialEntry = enter();
  try {
    return apply(jsFunction, thisObj, arguments_0);
  }
   finally {
    initialEntry && $flushFinallyCommands(($clinit() , INSTANCE));
    --entryDepth;
  }
}

var entryDepth = 0, sNextHashId = 0;
function $clinit(){
  $clinit = nullMethod;
  INSTANCE = new SchedulerImpl_0;
}

function $flushEntryCommands(this$static){
  var oldQueue, rescheduled;
  if (this$static.entryCommands) {
    rescheduled = null;
    do {
      oldQueue = this$static.entryCommands;
      this$static.entryCommands = null;
      rescheduled = runScheduledTasks(oldQueue, rescheduled);
    }
     while (this$static.entryCommands);
    this$static.entryCommands = rescheduled;
  }
}

function $flushFinallyCommands(this$static){
  var oldQueue, rescheduled;
  if (this$static.finallyCommands) {
    rescheduled = null;
    do {
      oldQueue = this$static.finallyCommands;
      this$static.finallyCommands = null;
      rescheduled = runScheduledTasks(oldQueue, rescheduled);
    }
     while (this$static.finallyCommands);
    this$static.finallyCommands = rescheduled;
  }
}

function SchedulerImpl_0(){
}

function push(queue, task){
  !queue && (queue = []);
  queue[queue.length] = task;
  return queue;
}

function runScheduledTasks(tasks, rescheduled){
  var $e0_0, i, j, t;
  for (i = 0 , j = tasks.length; i < j; ++i) {
    t = tasks[i];
    try {
      t[1]?t[0].nullMethod() && (rescheduled = push(rescheduled, t)):t[0].nullMethod();
    }
     catch ($e0) {
      $e0 = caught($e0);
      if (!instanceOf($e0, 4))
        throw $e0;
    }
  }
  return rescheduled;
}

function SchedulerImpl(){
}

_ = SchedulerImpl_0.prototype = SchedulerImpl.prototype = new Scheduler;
_.getClass$ = function getClass_10(){
  return Lcom_google_gwt_core_client_impl_SchedulerImpl_2_classLit;
}
;
_.castableTypeMap$ = {};
_.entryCommands = null;
_.finallyCommands = null;
var INSTANCE;
function extractNameFromToString(fnToString){
  var index, start, toReturn;
  toReturn = '';
  fnToString = $trim(fnToString);
  index = fnToString.indexOf('(');
  if (index != -1) {
    start = fnToString.indexOf('function') == 0?8:0;
    toReturn = $trim(fnToString.substr(start, index - start));
  }
  return toReturn.length > 0?toReturn:'anonymous';
}

function splice(arr, length_0){
  arr.length >= length_0 && arr.splice(0, length_0);
  return arr;
}

function $createStackTrace(e){
  var i, j, stack, stackTrace;
  stack = $inferFrom(instanceOfJso(e.e)?e.e:null);
  stackTrace = initDim(_3Ljava_lang_StackTraceElement_2_classLit, {}, 0, stack.length, 0);
  for (i = 0 , j = stackTrace.length; i < j; ++i) {
    stackTrace[i] = new StackTraceElement_0(stack[i]);
  }
  $setStackTrace(stackTrace);
}

function $fillInStackTrace(){
  var i, j, stack, stackTrace;
  stack = splice($inferFrom($makeException()), 2);
  stackTrace = initDim(_3Ljava_lang_StackTraceElement_2_classLit, {}, 0, stack.length, 0);
  for (i = 0 , j = stackTrace.length; i < j; ++i) {
    stackTrace[i] = new StackTraceElement_0(stack[i]);
  }
  $setStackTrace(stackTrace);
}

function $getProperties(e){
  var result = '';
  try {
    for (var prop in e) {
      if (prop != 'name' && (prop != 'message' && prop != 'toString')) {
        try {
          result += '\n ' + prop + ': ' + e[prop];
        }
         catch (ignored) {
        }
      }
    }
  }
   catch (ignored) {
  }
  return result;
}

function $makeException(){
  try {
    null.a();
  }
   catch (e) {
    return e;
  }
}

function $inferFrom(e){
  var i, j, stack;
  stack = e && e.stack?e.stack.split('\n'):[];
  for (i = 0 , j = stack.length; i < j; ++i) {
    stack[i] = extractNameFromToString(stack[i]);
  }
  return stack;
}

function GearsException(){
}

_ = GearsException.prototype = new Exception;
_.getClass$ = function getClass_11(){
  return Lcom_google_gwt_gears_client_GearsException_2_classLit;
}
;
_.castableTypeMap$ = {2:1, 6:1};
function $close(this$static){
  var $e0_0, ex;
  try {
    this$static.close();
  }
   catch ($e0) {
    $e0 = caught($e0);
    if (instanceOf($e0, 5)) {
      ex = $e0;
      throw new DatabaseException_0(ex.getDescription());
    }
     else 
      throw $e0;
  }
}

function $execute_0(this$static, sqlStatement, args){
  var $e0_0, ex;
  try {
    return this$static.execute(sqlStatement, toJavaScriptArray(args));
  }
   catch ($e0) {
    $e0 = caught($e0);
    if (instanceOf($e0, 5)) {
      ex = $e0;
      throw new DatabaseException_0(ex.getDescription());
    }
     else 
      throw $e0;
  }
}

function DatabaseException_0(message){
  $fillInStackTrace();
  this.detailMessage = message;
}

function DatabaseException(){
}

_ = DatabaseException_0.prototype = DatabaseException.prototype = new GearsException;
_.getClass$ = function getClass_12(){
  return Lcom_google_gwt_gears_client_database_DatabaseException_2_classLit;
}
;
_.castableTypeMap$ = {2:1, 3:1, 6:1};
function toJavaScriptArray(elements){
  var array, i;
  array = [];
  for (i = 0; i < elements.length; ++i) {
    array[i] = elements[i];
  }
  return array;
}

function Array_1(){
}

function createFromSeed(seedType, length_0){
  var array = new Array(length_0);
  if (seedType == 3) {
    for (var i = 0; i < length_0; ++i) {
      var value = new Object;
      value.l = value.m = value.h = 0;
      array[i] = value;
    }
  }
   else if (seedType > 0) {
    var value = [null, 0, false][seedType];
    for (var i = 0; i < length_0; ++i) {
      array[i] = value;
    }
  }
  return array;
}

function initDim(arrayClass, castableTypeMap, queryId, length_0, seedType){
  var result;
  result = createFromSeed(seedType, length_0);
  $clinit_0();
  wrapArray(result, expandoNames_0, expandoValues_0);
  result.arrayClass$ = arrayClass;
  result.castableTypeMap$ = castableTypeMap;
  return result;
}

function initValues(arrayClass, castableTypeMap, queryId, array){
  $clinit_0();
  wrapArray(array, expandoNames_0, expandoValues_0);
  array.arrayClass$ = arrayClass;
  array.castableTypeMap$ = castableTypeMap;
  return array;
}

function Array_0(){
}

_ = Array_1.prototype = Array_0.prototype = new Object_0;
_.getClass$ = function getClass_13(){
  return this.arrayClass$;
}
;
_.castableTypeMap$ = {};
_.arrayClass$ = null;
function $clinit_0(){
  $clinit_0 = nullMethod;
  expandoNames_0 = [];
  expandoValues_0 = [];
  initExpandos(new Array_1, expandoNames_0, expandoValues_0);
}

function initExpandos(protoType, expandoNames, expandoValues){
  var i = 0, value;
  for (var name_0 in protoType) {
    if (value = protoType[name_0]) {
      expandoNames[i] = name_0;
      expandoValues[i] = value;
      ++i;
    }
  }
}

function wrapArray(array, expandoNames, expandoValues){
  $clinit_0();
  for (var i = 0, c = expandoNames.length; i < c; ++i) {
    array[expandoNames[i]] = expandoValues[i];
  }
}

var expandoNames_0, expandoValues_0;
function instanceOf(src, dstId){
  return src != null && (src.castableTypeMap$ && !!src.castableTypeMap$[dstId]);
}

function instanceOfJso(src){
  return src != null && (src.typeMarker$ != nullMethod && !(src.castableTypeMap$ && !!src.castableTypeMap$[1]));
}

function init(){
  var runtimeValue;
  !!$stats && onModuleStart('com.google.gwt.user.client.UserAgentAsserter');
  runtimeValue = $getRuntimeValue();
  $equals(runtimeValue) || ($wnd.alert('ERROR: Possible problem with your *.gwt.xml module file.\nThe compile time user.agent value (gecko1_8) does not match the runtime user.agent value (' + runtimeValue + '). Expect more errors.\n') , undefined);
  !!$stats && onModuleStart('com.bedatadriven.rebar.sql.worker.client.GearsSqlWorker');
  $registerWorker(new GearsSqlWorker_0);
}

function caught(e){
  if (e != null && (e.castableTypeMap$ && !!e.castableTypeMap$[6])) {
    return e;
  }
  return new JavaScriptException_0(e);
}

function onModuleStart(mainClassName){
  return $stats({moduleName:$moduleName, sessionId:$sessionId, subSystem:'startup', evtGroup:'moduleStartup', millis:(new Date).getTime(), type:'onModuleLoadStart', className:mainClassName});
}

function $getRuntimeValue(){
  var ua = navigator.userAgent.toLowerCase();
  var makeVersion = function(result){
    return parseInt(result[1]) * 1000 + parseInt(result[2]);
  }
  ;
  if (function(){
    return ua.indexOf('opera') != -1;
  }
  ())
    return 'opera';
  if (function(){
    return ua.indexOf('webkit') != -1;
  }
  ())
    return 'safari';
  if (function(){
    return ua.indexOf('msie') != -1 && $doc.documentMode >= 9;
  }
  ())
    return 'ie9';
  if (function(){
    return ua.indexOf('msie') != -1 && $doc.documentMode >= 8;
  }
  ())
    return 'ie8';
  if (function(){
    var result = /msie ([0-9]+)\.([0-9]+)/.exec(ua);
    if (result && result.length == 3)
      return makeVersion(result) >= 6000;
  }
  ())
    return 'ie6';
  if (function(){
    return ua.indexOf('gecko') != -1;
  }
  ())
    return 'gecko1_8';
  return 'unknown';
}

function Class_0(){
}

function createForArray(seedName){
  var clazz;
  clazz = new Class_0;
  clazz.typeName = 'Class$' + (seedName != null?seedName:'' + (clazz.$H || (clazz.$H = ++sNextHashId)));
  clazz.modifiers = 4;
  return clazz;
}

function createForClass(seedName){
  var clazz;
  clazz = new Class_0;
  clazz.typeName = 'Class$' + (seedName != null?seedName:'' + (clazz.$H || (clazz.$H = ++sNextHashId)));
  return clazz;
}

function Class(){
}

_ = Class_0.prototype = Class.prototype = new Object_0;
_.getClass$ = function getClass_14(){
  return Ljava_lang_Class_2_classLit;
}
;
_.toString$ = function toString_2(){
  return ((this.modifiers & 2) != 0?'interface ':(this.modifiers & 1) != 0?'':'class ') + this.typeName;
}
;
_.castableTypeMap$ = {};
_.modifiers = 0;
_.typeName = null;
function toPowerOfTwoString(value){
  var buf, digits, pos;
  buf = initDim(_3C_classLit, {}, -1, 8, 1);
  digits = ($clinit_1() , digits_0);
  pos = 7;
  if (value >= 0) {
    while (value > 15) {
      buf[pos--] = digits[value & 15];
      value >>= 4;
    }
  }
   else {
    while (pos > 0) {
      buf[pos--] = digits[value & 15];
      value >>= 4;
    }
  }
  buf[pos] = digits[value & 15];
  return __valueOf(buf, pos, 8);
}

function NullPointerException_0(){
  $fillInStackTrace();
}

function NullPointerException(){
}

_ = NullPointerException_0.prototype = NullPointerException.prototype = new RuntimeException;
_.getClass$ = function getClass_15(){
  return Ljava_lang_NullPointerException_2_classLit;
}
;
_.castableTypeMap$ = {2:1, 4:1, 6:1};
function $clinit_1(){
  $clinit_1 = nullMethod;
  digits_0 = initValues(_3C_classLit, {}, -1, [48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122]);
}

var digits_0;
function StackTraceElement_0(methodName){
  this.className = 'Unknown';
  this.methodName = methodName;
  this.lineNumber = -1;
}

function StackTraceElement(){
}

_ = StackTraceElement_0.prototype = StackTraceElement.prototype = new Object_0;
_.getClass$ = function getClass_16(){
  return Ljava_lang_StackTraceElement_2_classLit;
}
;
_.toString$ = function toString_3(){
  return this.className + '.' + this.methodName + '(Unknown Source' + (this.lineNumber >= 0?':' + this.lineNumber:'') + ')';
}
;
_.castableTypeMap$ = {};
_.className = null;
_.lineNumber = 0;
_.methodName = null;
function $equals(other){
  if (other == null) {
    return false;
  }
  return String('gecko1_8') == other;
}

function $trim(this$static){
  if (this$static.length == 0 || this$static[0] > ' ' && this$static[this$static.length - 1] > ' ') {
    return this$static;
  }
  var r1 = this$static.replace(/^(\s*)/, '');
  var r2 = r1.replace(/\s*$/, '');
  return r2;
}

function __valueOf(x, start, end){
  x = x.slice(start, end);
  return String.fromCharCode.apply(null, x);
}

_ = String.prototype;
_.getClass$ = function getClass_17(){
  return Ljava_lang_String_2_classLit;
}
;
_.hashCode$ = function hashCode_1(){
  return getHashCode(this);
}
;
_.toString$ = function toString_4(){
  return this;
}
;
_.castableTypeMap$ = {1:1};
function $clinit_2(){
  $clinit_2 = nullMethod;
  back_0 = {};
  front = {};
}

function compute(str){
  var hashCode, i, n, nBatch;
  hashCode = 0;
  n = str.length;
  nBatch = n - 4;
  i = 0;
  while (i < nBatch) {
    hashCode = str.charCodeAt(i + 3) + 31 * (str.charCodeAt(i + 2) + 31 * (str.charCodeAt(i + 1) + 31 * (str.charCodeAt(i) + 31 * hashCode))) | 0;
    i += 4;
  }
  while (i < n) {
    hashCode = hashCode * 31 + str.charCodeAt(i++);
  }
  return hashCode | 0;
}

function getHashCode(str){
  $clinit_2();
  var key = ':' + str;
  var result = front[key];
  if (result != null) {
    return result;
  }
  result = back_0[key];
  result == null && (result = compute(str));
  increment();
  return front[key] = result;
}

function increment(){
  if (count == 256) {
    back_0 = front;
    front = {};
    count = 0;
  }
  ++count;
}

var back_0, count = 0, front;
var $entry = entry;
function gwtOnLoad(errFn, modName, modBase, softPermutationId){
  $moduleName = modName;
  $moduleBase = modBase;
  if (errFn)
    try {
      $entry(init)();
    }
     catch (e) {
      errFn(modName);
    }
   else {
    $entry(init)();
  }
}

var Ljava_lang_Object_2_classLit = createForClass('Object_0'), Ljava_lang_String_2_classLit = createForClass('String_0'), _3Ljava_lang_String_2_classLit = createForArray('Array_0'), Ljava_lang_Throwable_2_classLit = createForClass('Throwable'), Ljava_lang_StackTraceElement_2_classLit = createForClass('StackTraceElement'), _3Ljava_lang_StackTraceElement_2_classLit = createForArray('Array_0'), Ljava_lang_Exception_2_classLit = createForClass('Exception'), Ljava_lang_RuntimeException_2_classLit = createForClass('RuntimeException'), Lcom_bedatadriven_rebar_worker_client_AbstractWorkerEntryPoint_2_classLit = createForClass('AbstractWorkerEntryPoint'), Lcom_bedatadriven_rebar_sql_worker_client_GearsSqlWorker_2_classLit = createForClass('GearsSqlWorker'), Lcom_bedatadriven_rebar_sql_worker_client_GearsUpdateExecutor_2_classLit = createForClass('GearsUpdateExecutor'), Lcom_bedatadriven_rebar_sql_worker_client_WorkerLogger_2_classLit = createForClass('WorkerLogger'), Lcom_google_gwt_core_client_JavaScriptException_2_classLit = createForClass('JavaScriptException'), Lcom_google_gwt_core_client_JavaScriptObject_2_classLit = createForClass('JavaScriptObject'), Lcom_google_gwt_core_client_Scheduler_2_classLit = createForClass('Scheduler'), Lcom_google_gwt_core_client_impl_SchedulerImpl_2_classLit = createForClass('SchedulerImpl'), Lcom_google_gwt_gears_client_GearsException_2_classLit = createForClass('GearsException'), Lcom_google_gwt_gears_client_database_DatabaseException_2_classLit = createForClass('DatabaseException'), _3C_classLit = createForArray('Array_0'), Ljava_lang_Class_2_classLit = createForClass('Class'), Ljava_lang_NullPointerException_2_classLit = createForClass('NullPointerException');
function $stats(a){
}

$strongName = '3056A9E6EDF7DB96BAA1B5AFC765CC4F';
navigator = {userAgent:'gears'};
window = {};
window.location = {};
window.google = google;
$wnd = window;
$doc = {};
$sessionId = 1;
$wnd.alert = function(msg){
}
;
$gtimer = google.gears.factory.create('beta.timer');
$wnd.setTimeout = function(a, b){
  return $gtimer.setTimeout(a, b);
}
;
$wnd.setInterval = function(a, b){
  return $gtimer.setInterval(a, b);
}
;
$wnd.clearTimeout = function(id){
  return $gtimer.clearTimeout(id);
}
;
$wnd.clearInterval = function(id){
  return $gtimer.clearInterval(id);
}
;
gwtOnLoad(null, 'GearsSqlWorker', '');
