import sys
from concurrent import futures

sys.path.append('../../../build/generated/source/proto/main/python')
sys.path.append('../../../build/generated/source/proto/main/grpc/echo')
import grpc

import echomessage_pb2 as em
import echomessage_pb2_grpc as rpc


class Echoer(rpc.EchoServicer):

    def parrot(self, request, context):
        print("Received from client: " + request.message)
        return em.ServerResponse(message='%s' % request.message)


class EchoServer(object):
    def __init__(self, port):
        self.port = port

    def run(self):
        print("Server Running ...")
        server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
        rpc.add_EchoServicer_to_server(Echoer(), server)
        server.add_insecure_port('[::]:{}'.format(port))
        server.start()
        server.wait_for_termination()


if __name__ == '__main__':
    if len(sys.argv) != 2:
        raise ValueError("Expected arguments: <port(int)>")
    print(sys.argv)
    _, port = sys.argv  # port
    server = EchoServer(int(port))
    server.run()

