import sys
import grpc

sys.path.append('../../../build/generated/source/proto/main/python')
sys.path.append('../../../build/generated/source/proto/main/grpc/echo')
import echomessage_pb2 as em
import echomessage_pb2_grpc as rpc

class EchoClient(object):

    def __init__(self, host, port, message):
        self.host = host
        self.port = port
        self.message = message

    def askServerToParrot(self):
        with grpc.insecure_channel("{}:{}".format(self.host, self.port)) as channel:
            stub = rpc.EchoStub(channel)
            response = stub.parrot(em.ClientRequest(message=self.message))
        print("Received from server: " + response.message)


if __name__ == '__main__':
    if len(sys.argv) != 4:
            raise ValueError("Expected arguments: <host(String)> <port(int)> <message(String)>")
    print(sys.argv)
    _, host, port, message = sys.argv  # host, port, message
    client = EchoClient(host, int(port), message)
    client.askServerToParrot()

