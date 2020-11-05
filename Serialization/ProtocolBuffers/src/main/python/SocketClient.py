import socket
import sys
import json
# specify where the generated protofiles are
sys.path.append('../../../build/generated/source/proto/main/python')

from operation_pb2 import Operation
from response_pb2 import Response
from google.protobuf.internal.encoder import _VarintBytes
from google.protobuf.internal.decoder import _DecodeVarint32


class SocketClient(object):
    def __init__(self, host, port, filename):
        self.host = host
        self.port = port
        self.filename = "../resources/"+filename

    OPERATION_TYPES = {
        "add": Operation.OperationType.ADD,
        "sub": Operation.OperationType.SUB,
        "mul": Operation.OperationType.MUL,
        "div": Operation.OperationType.DIV,
    }
    RESPONSE_TYPES = {
        "json": Operation.ResponseType.JSON,
        "string": Operation.ResponseType.STRING
    }

    def connect(self):

        with open(self.filename, "r") as _file:
            data = json.load(_file)

        try:
            # connect to the server
            serverSock = socket.socket()
            serverSock.connect((self.host, self.port))
        except socket.error:
            print('Failed to create socket')
            sys.exit()

        operation = self.generateOperationObject(data)

        # send message
        serverSock.send(_VarintBytes(operation.ByteSize()))
        serverSock.send(operation.SerializeToString())

        response = Response()

        # receive message
        received = serverSock.recv(1024)

        # convert message to PB object
        msgLen, newPos = _DecodeVarint32(received, 0)
        response.ParseFromString(received[newPos:newPos + msgLen])

        print("Result is: ", response.resultString)

        serverSock.close()  # close the connection

    def generateOperationObject(self, data):
        operation = Operation()
        try:
            header = data["header"]
            payload = data["payload"]
            operation.val1 = payload["num1"]
            operation.val2 = payload["num2"]
            operation.base = header["base"]
            operation.operationType = self.OPERATION_TYPES[header["operation"].lower()]
            operation.responseType = self.RESPONSE_TYPES[header["response"].lower()]
            return operation
        except KeyError as err:
            raise KeyError  # handle exception


if __name__ == "__main__":
    if len(sys.argv) != 4:
            raise ValueError("Expected arguments: <host(String)> <port(int)> <data(json file)>")
    print(sys.argv)
    _, host, port, filename = sys.argv  # host, port, filename
    socketClient = SocketClient(host, int(port), filename)
    socketClient.connect()

